-- ============================================================
-- 基于 WebGIS 的光缆通信资源管理系统 - 数据库设计
-- 参考论文: 《基于 WebGIS 的光缆通信资源管理系统的研究与实现》
-- ============================================================

-- 1. 启用 PostGIS 扩展
CREATE EXTENSION IF NOT EXISTS postgis;

-- 2. 删除旧的枚举类型（如果存在）
DROP TYPE IF EXISTS resource_type CASCADE;
DROP TYPE IF EXISTS routing_type CASCADE;
DROP TYPE IF EXISTS cable_level CASCADE;
DROP TYPE IF EXISTS laying_style CASCADE;

-- 3. 创建资源类型枚举
CREATE TYPE resource_type AS ENUM (
    'office',        -- 00 营业厅
    'machine_room',  -- 01 机房
    'base_station',  -- 02 基站
    'pole',          -- 03 电杆
    'manhole',      -- 04 人井
    'user_terminal',-- 05 终端用户
    'cabinet'       -- 06 光交箱
);

-- 4. 创建路由类型枚举（5种）
CREATE TYPE routing_type AS ENUM (
    'buried',    -- 直埋
    'aerial',    -- 架空
    'pipeline',  -- 管道
    'upground',  -- 引上
    'wall_mount' -- 挂墙
);

-- 5. 创建光缆级别枚举
CREATE TYPE cable_level AS ENUM (
    'national',      -- 一干 跨省
    'provincial',    -- 二干 省市之间
    'local_backbone',-- 本地主干 县区之间
    'access'         -- 本地接入网 县区内
);

-- 6. 创建敷设方式枚举
CREATE TYPE laying_style AS ENUM (
    'buried',    -- 直埋
    'aerial',    -- 架空
    'pipeline',  -- 管道
    'upground',  -- 引上
    'wall_mount' -- 挂墙
);

-- ============================================================
-- 点资源表 (resource_point / res)
-- ============================================================
CREATE TABLE IF NOT EXISTS resource_point (
    resource_point_id BIGSERIAL PRIMARY KEY,
    type resource_type NOT NULL,              -- 资源类型
    name VARCHAR(100),                        -- 资源名称
    address VARCHAR(200),                     -- 地址
    status INTEGER DEFAULT 0,                 -- 状态: 正常(0), 运行(1), 异常(2)
    geom GEOMETRY(POINT, 4326) NOT NULL,     -- 空间位置 (WGS84)
    props JSONB DEFAULT '{}'::jsonb,          -- 动态扩展属性
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 路由段表 (routing / res_routing)
-- 定义: 两个资源点之间的物理通道（管道/电线杆等）
-- ============================================================
CREATE TABLE IF NOT EXISTS routing (
    routing_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),                       -- 路由名称
    start_point_id BIGINT NOT NULL,          -- 起始资源点ID
    end_point_id BIGINT NOT NULL,            -- 终点资源点ID
    type routing_type DEFAULT 'pipeline',    -- 路由类型
    length NUMERIC(10, 2) DEFAULT 0,         -- 路由长度(米)
    cables_count INTEGER DEFAULT 0,          -- 光缆段数量
    geom GEOMETRY(LINESTRING, 4326),        -- 路由路径
    props JSONB DEFAULT '{}'::jsonb,          -- 动态扩展属性
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (start_point_id) REFERENCES resource_point(resource_point_id),
    FOREIGN KEY (end_point_id) REFERENCES resource_point(resource_point_id)
);

-- ============================================================
-- 光缆段表 (cableseg / fiber_segment)
-- 定义: 铺设在路由上的实际光缆，是基本单位
-- ============================================================
CREATE TABLE IF NOT EXISTS fiber_segment (
    segment_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),                         -- 光缆段名称
    start_point_id BIGINT NOT NULL,           -- 起始资源点ID
    end_point_id BIGINT NOT NULL,             -- 终点资源点ID
    routing_id BIGINT,                        -- 所属路由ID
    cable_level cable_level DEFAULT 'access', -- 光缆级别
    length NUMERIC(10, 2) DEFAULT 0,          -- 光缆长度(米)
    fiber_count INTEGER DEFAULT 1,            -- 光纤芯数
    tube_count INTEGER DEFAULT 1,             -- 束管数
    fibers_per_tube INTEGER DEFAULT 4,        -- 每束管光纤数
    laying_style laying_style DEFAULT 'pipeline', -- 敷设方式
    geom GEOMETRY(LINESTRING, 4326),         -- 光缆路径
    props JSONB DEFAULT '{}'::jsonb,          -- 动态扩展属性
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (start_point_id) REFERENCES resource_point(resource_point_id),
    FOREIGN KEY (end_point_id) REFERENCES resource_point(resource_point_id),
    FOREIGN KEY (routing_id) REFERENCES routing(routing_id)
);

-- ============================================================
-- 全程缆表 (cable)
-- 定义: 端到端的完整光缆线路，如"京沪干线"
-- ============================================================
CREATE TABLE IF NOT EXISTS cable (
    cable_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,               -- 全程缆名称
    cable_level cable_level DEFAULT 'access', -- 光缆级别
    start_point_id BIGINT,                    -- 起始资源点ID
    end_point_id BIGINT,                      -- 终点资源点ID
    total_length NUMERIC(10, 2) DEFAULT 0,    -- 总长度(米)
    total_fiber_count INTEGER DEFAULT 0,      -- 总芯数
    operator VARCHAR(100),                    -- 运营商
    status INTEGER DEFAULT 0,                 -- 状态
    props JSONB DEFAULT '{}'::jsonb,          -- 动态扩展属性
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (start_point_id) REFERENCES resource_point(resource_point_id),
    FOREIGN KEY (end_point_id) REFERENCES resource_point(resource_point_id)
);

-- ============================================================
-- 中继段表 (cable_repeater)
-- 定义: 需要中继器放大的光缆段落
-- ============================================================
CREATE TABLE IF NOT EXISTS cable_repeater (
    repeater_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),                        -- 中继段名称
    start_point_id BIGINT,                    -- 起始资源点ID
    end_point_id BIGINT,                      -- 终点资源点ID
    repeater_point_id BIGINT,                 -- 中继点资源ID（中继器位置）
    length NUMERIC(10, 2) DEFAULT 0,          -- 中继段长度
    loss_budget NUMERIC(10, 2),               -- 损耗预算
    status INTEGER DEFAULT 0,                 -- 状态
    props JSONB DEFAULT '{}'::jsonb,          -- 动态扩展属性
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (start_point_id) REFERENCES resource_point(resource_point_id),
    FOREIGN KEY (end_point_id) REFERENCES resource_point(resource_point_id),
    FOREIGN KEY (repeater_point_id) REFERENCES resource_point(resource_point_id)
);

-- ============================================================
-- 光缆段与资源点关联表 (R_1N_cableseg_res)
-- 定义: 记录光缆段经过的资源点（人井、接头盒等）
-- ============================================================
CREATE TABLE IF NOT EXISTS r_1n_cableseg_res (
    id BIGSERIAL PRIMARY KEY,
    segment_id BIGINT NOT NULL,               -- 光缆段ID
    res_id BIGINT NOT NULL,                   -- 资源点ID
    res_connector_id BIGINT,                  -- 关联的接头盒ID
    sequence INTEGER DEFAULT 0,               -- 顺序号（光缆段上的位置顺序）
    props JSONB DEFAULT '{}'::jsonb,          -- 动态扩展属性
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (segment_id) REFERENCES fiber_segment(segment_id),
    FOREIGN KEY (res_id) REFERENCES resource_point(resource_point_id),
    UNIQUE(segment_id, res_id, sequence)
);

-- ============================================================
-- 全程缆与光缆段关联表 (R_1N_cable_cableseg)
-- 定义: 一条全程缆包含多个光缆段
-- ============================================================
CREATE TABLE IF NOT EXISTS r_1n_cable_cableseg (
    id BIGSERIAL PRIMARY KEY,
    cable_id BIGINT NOT NULL,                 -- 全程缆ID
    segment_id BIGINT NOT NULL,               -- 光缆段ID
    sequence INTEGER DEFAULT 0,               -- 顺序号（在全程缆中的位置）
    props JSONB DEFAULT '{}'::jsonb,          -- 动态扩展属性
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cable_id) REFERENCES cable(cable_id),
    FOREIGN KEY (segment_id) REFERENCES fiber_segment(segment_id),
    UNIQUE(cable_id, segment_id)
);

-- ============================================================
-- 路由与光缆段关联表 (R_1N_routing_cableseg)
-- 定义: 一条路由上可以有多条光缆段
-- ============================================================
CREATE TABLE IF NOT EXISTS r_1n_routing_cableseg (
    id BIGSERIAL PRIMARY KEY,
    routing_id BIGINT NOT NULL,               -- 路由ID
    segment_id BIGINT NOT NULL,               -- 光缆段ID
    props JSONB DEFAULT '{}'::jsonb,          -- 动态扩展属性
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (routing_id) REFERENCES routing(routing_id),
    FOREIGN KEY (segment_id) REFERENCES fiber_segment(segment_id),
    UNIQUE(routing_id, segment_id)
);

-- ============================================================
-- 中继段与光缆段关联表 (R_1N_repeater_cableseg)
-- 定义: 一个中继段包含多个光缆段
-- ============================================================
CREATE TABLE IF NOT EXISTS r_1n_repeater_cableseg (
    id BIGSERIAL PRIMARY KEY,
    repeater_id BIGINT NOT NULL,              -- 中继段ID
    segment_id BIGINT NOT NULL,               -- 光缆段ID
    sequence INTEGER DEFAULT 0,               -- 顺序号
    props JSONB DEFAULT '{}'::jsonb,          -- 动态扩展属性
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (repeater_id) REFERENCES cable_repeater(repeater_id),
    FOREIGN KEY (segment_id) REFERENCES fiber_segment(segment_id),
    UNIQUE(repeater_id, segment_id)
);

-- ============================================================
-- 索引创建
-- ============================================================

-- 点资源索引
CREATE INDEX IF NOT EXISTS idx_resource_point_geom ON resource_point USING GIST (geom);
CREATE INDEX IF NOT EXISTS idx_resource_point_type ON resource_point (type);
CREATE INDEX IF NOT EXISTS idx_resource_point_status ON resource_point (status);

-- 路由段索引
CREATE INDEX IF NOT EXISTS idx_routing_geom ON routing USING GIST (geom);
CREATE INDEX IF NOT EXISTS idx_routing_start ON routing (start_point_id);
CREATE INDEX IF NOT EXISTS idx_routing_end ON routing (end_point_id);
CREATE INDEX IF NOT EXISTS idx_routing_type ON routing (type);

-- 光缆段索引
CREATE INDEX IF NOT EXISTS idx_fiber_segment_geom ON fiber_segment USING GIST (geom);
CREATE INDEX IF NOT EXISTS idx_fiber_segment_routing ON fiber_segment (routing_id);
CREATE INDEX IF NOT EXISTS idx_fiber_segment_start ON fiber_segment (start_point_id);
CREATE INDEX IF NOT EXISTS idx_fiber_segment_end ON fiber_segment (end_point_id);
CREATE INDEX IF NOT EXISTS idx_fiber_segment_level ON fiber_segment (cable_level);

-- 全程缆索引
CREATE INDEX IF NOT EXISTS idx_cable_level ON cable (cable_level);
CREATE INDEX IF NOT EXISTS idx_cable_start ON cable (start_point_id);
CREATE INDEX IF NOT EXISTS idx_cable_end ON cable (end_point_id);

-- 中继段索引
CREATE INDEX IF NOT EXISTS idx_cable_repeater_start ON cable_repeater (start_point_id);
CREATE INDEX IF NOT EXISTS idx_cable_repeater_end ON cable_repeater (end_point_id);
CREATE INDEX IF NOT EXISTS idx_cable_repeater_point ON cable_repeater (repeater_point_id);

-- 关联表索引
CREATE INDEX IF NOT EXISTS idx_r_1n_cableseg_res_segment ON r_1n_cableseg_res (segment_id);
CREATE INDEX IF NOT EXISTS idx_r_1n_cableseg_res_res ON r_1n_cableseg_res (res_id);
CREATE INDEX IF NOT EXISTS idx_r_1n_cable_cableseg_cable ON r_1n_cable_cableseg (cable_id);
CREATE INDEX IF NOT EXISTS idx_r_1n_cable_cableseg_segment ON r_1n_cable_cableseg (segment_id);
CREATE INDEX IF NOT EXISTS idx_r_1n_routing_cableseg_routing ON r_1n_routing_cableseg (routing_id);
CREATE INDEX IF NOT EXISTS idx_r_1n_routing_cableseg_segment ON r_1n_routing_cableseg (segment_id);
CREATE INDEX IF NOT EXISTS idx_r_1n_repeater_cableseg_repeater ON r_1n_repeater_cableseg (repeater_id);
CREATE INDEX IF NOT EXISTS idx_r_1n_repeater_cableseg_segment ON r_1n_repeater_cableseg (segment_id);

-- ============================================================
-- 示例数据
-- ============================================================

-- 插入点资源（使用WKT格式存储坐标）
INSERT INTO resource_point (type, name, address, status, geom, props) VALUES
('pole', '电杆1', '河北省保定市新市区', 0, ST_SetSRID(ST_MakePoint(116.4074, 39.9042), 4326), '{"material": "concrete", "height": "8m"}'),
('manhole', '人井1', '河北省保定市新市区', 0, ST_SetSRID(ST_MakePoint(116.4174, 39.9142), 4326), '{"depth": "2m", "type": "concrete"}'),
('machine_room', '保定机房', '河北省保定市朝阳大街', 0, ST_SetSRID(ST_MakePoint(116.4274, 39.9242), 4326), '{"building": "A座", "floor": "1"}'),
('cabinet', '光交箱1', '河北省保定市新市区', 0, ST_SetSRID(ST_MakePoint(116.4374, 39.9342), 4326), '{"capacity": "288", "type": "outdoor"}'),
('base_station', '基站1', '河北省保定市高新区', 0, ST_SetSRID(ST_MakePoint(116.4474, 39.9442), 4326), '{"operator": "ChinaMobile"}');

-- 插入路由段
INSERT INTO routing (name, start_point_id, end_point_id, type, length, geom) VALUES
('路由段1', 1, 2, 'pipeline', 1500, ST_SetSRID(ST_MakeLine(ST_MakePoint(116.4074, 39.9042), ST_MakePoint(116.4174, 39.9142)), 4326)),
('路由段2', 2, 3, 'buried', 1200, ST_SetSRID(ST_MakeLine(ST_MakePoint(116.4174, 39.9142), ST_MakePoint(116.4274, 39.9242)), 4326));

-- 插入光缆段
INSERT INTO fiber_segment (name, start_point_id, end_point_id, routing_id, cable_level, length, fiber_count, tube_count, laying_style, geom) VALUES
('光缆段1', 1, 2, 1, 'access', 1500, 24, 1, 'pipeline', ST_SetSRID(ST_MakeLine(ST_MakePoint(116.4074, 39.9042), ST_MakePoint(116.4174, 39.9142)), 4326)),
('光缆段2', 2, 3, 2, 'access', 1200, 24, 1, 'buried', ST_SetSRID(ST_MakeLine(ST_MakePoint(116.4174, 39.9142), ST_MakePoint(116.4274, 39.9242)), 4326)),
('光缆段3', 3, 4, 2, 'access', 1100, 48, 4, 'buried', ST_SetSRID(ST_MakeLine(ST_MakePoint(116.4274, 39.9242), ST_MakePoint(116.4374, 39.9342)), 4326));

-- 插入全程缆示例
INSERT INTO cable (name, cable_level, start_point_id, end_point_id, total_length, total_fiber_count, operator, status, props) VALUES
('保定-石家庄干线', 'provincial', 1, 4, 3800, 48, '中国电信', 0, '{"construction_date": "2023-01", "maintenance_unit": "保定分公司"}');

-- 插入全程缆与光缆段关联
INSERT INTO r_1n_cable_cableseg (cable_id, segment_id, sequence) VALUES
(1, 1, 1),
(1, 2, 2),
(1, 3, 3);

-- 插入路由与光缆段关联
INSERT INTO r_1n_routing_cableseg (routing_id, segment_id) VALUES
(1, 1),
(2, 2),
(2, 3);

-- 插入光缆段与资源点关联（记录光缆段经过的中间资源点）
INSERT INTO r_1n_cableseg_res (segment_id, res_id, sequence, props) VALUES
(1, 1, 1, '{"role": "start"}'),
(1, 2, 2, '{"role": "end"}'),
(2, 2, 1, '{"role": "start"}'),
(2, 3, 2, '{"role": "end"}');
