-- 1. 启用 PostGIS 扩展（需有 superuser 权限，若已启用可忽略）
CREATE EXTENSION IF NOT EXISTS postgis;

-- 2. 创建资源类型枚举
CREATE TYPE resource_type AS ENUM (
    'pole',      -- 电杆
    'manhole',   -- 人井
    'office',    -- 营业厅
    'cabinet',   -- 光交箱
    'base_station', -- 基站
    'distribution_box', -- 分纤箱
    'user_terminal' -- 用户终端
);

-- 3. 创建资源点表（使用 enum 类型标识资源类型）
CREATE TABLE IF NOT EXISTS resource_point (
    point_id BIGSERIAL PRIMARY KEY,
    type resource_type NOT NULL, -- 资源类型
    name VARCHAR(100), -- 资源名称
    address VARCHAR(255), -- 资源地址
    status VARCHAR(20) DEFAULT 'normal', -- 资源状态
    geom GEOMETRY(POINT, 4326) NOT NULL, -- 空间位置
    props JSONB DEFAULT '{}'::jsonb, -- 动态扩展属性
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. 创建光缆段表
CREATE TABLE IF NOT EXISTS fiber_segment (
    segment_id BIGSERIAL PRIMARY KEY,
    length NUMERIC(10, 2) DEFAULT 0, -- 光缆段长度
    start_point_id BIGINT NOT NULL, -- 起点资源点ID
    end_point_id BIGINT NOT NULL, -- 终点资源点ID
    geom GEOMETRY(LINESTRING, 4326), -- 存储光缆段路径
    props JSONB DEFAULT '{}'::jsonb, -- 动态扩展属性
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    -- 添加外键约束
    FOREIGN KEY (start_point_id) REFERENCES resource_point(point_id),
    FOREIGN KEY (end_point_id) REFERENCES resource_point(point_id)
);

-- 4. 添加索引
CREATE INDEX IF NOT EXISTS idx_resource_point_geom ON resource_point USING GIST (geom);
CREATE INDEX IF NOT EXISTS idx_resource_point_type ON resource_point (type);
CREATE INDEX IF NOT EXISTS idx_resource_point_identifier ON resource_point (identifier);
CREATE INDEX IF NOT EXISTS idx_fiber_segment_geom ON fiber_segment USING GIST (geom);
CREATE INDEX IF NOT EXISTS idx_fiber_segment_start_point ON fiber_segment (start_point_id);
CREATE INDEX IF NOT EXISTS idx_fiber_segment_end_point ON fiber_segment (end_point_id);

-- 5. 插入示例数据
-- 插入电杆数据
INSERT INTO resource_point (type, identifier, name, status, geom, props) VALUES
('pole', 'P-001', '电杆1', 'normal', ST_SetSRID(ST_MakePoint(116.4074, 39.9042), 4326), '{"height": "8m", "material": "concrete"}');

-- 插入人井数据
INSERT INTO resource_point (type, identifier, name, status, geom, props) VALUES
('manhole', 'MH-001', '人井1', 'normal', ST_SetSRID(ST_MakePoint(116.4174, 39.9142), 4326), '{"depth": "2m", "capacity": "10"}');

-- 插入营业厅数据
INSERT INTO resource_point (type, identifier, name, address, status, geom, props) VALUES
('office', 'O-001', '北京营业厅', '北京市朝阳区建国路88号', 'normal', ST_SetSRID(ST_MakePoint(116.4274, 39.9242), 4326), '{"phone": "010-12345678", "manager": "张三"}');

-- 插入光交箱数据
INSERT INTO resource_point (type, identifier, name, status, geom, props) VALUES
('cabinet', 'C-001', '光交箱1', 'normal', ST_SetSRID(ST_MakePoint(116.4374, 39.9342), 4326), '{"capacity": "288", "type": "indoor"}');

-- 插入光缆段数据（连接电杆和人井）
INSERT INTO fiber_segment (identifier, length, start_point_id, end_point_id, geom, props) VALUES
('FS-001', 100, 1, 2, ST_SetSRID(ST_MakeLine(ST_MakePoint(116.4074, 39.9042), ST_MakePoint(116.4174, 39.9142)), 4326), '{"type": "single_mode", "core_count": "24"}');

-- 插入光缆段数据（连接人井和营业厅）
INSERT INTO fiber_segment (identifier, length, start_point_id, end_point_id, geom, props) VALUES
('FS-002', 150, 2, 3, ST_SetSRID(ST_MakeLine(ST_MakePoint(116.4174, 39.9142), ST_MakePoint(116.4274, 39.9242)), 4326), '{"type": "single_mode", "core_count": "24"}');

-- 插入光缆段数据（连接营业厅和光交箱）
INSERT INTO fiber_segment (identifier, length, start_point_id, end_point_id, geom, props) VALUES
('FS-003', 200, 3, 4, ST_SetSRID(ST_MakeLine(ST_MakePoint(116.4274, 39.9242), ST_MakePoint(116.4374, 39.9342)), 4326), '{"type": "single_mode", "core_count": "48"}');





