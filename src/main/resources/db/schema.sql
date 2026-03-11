-- 资源表（支持 PostGIS 空间查询）
CREATE EXTENSION IF NOT EXISTS postgis

CREATE TABLE IF NOT EXISTS resources (
    resources_id BIGSERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL COMMENT '资源类型：manhole, pole, cabinet, fiber_segment, office 等',
    geom GEOMETRY(POINT, 4326) NOT NULL COMMENT '空间位置',
    props JSONB DEFAULT '{}'::jsonb COMMENT '属性字段（动态扩展）',
    created_at TIMESTAMP with time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP with time zone DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)

-- 创建空间索引
CREATE INDEX IF NOT EXISTS idx_resources_geom ON resources USING GIST (geom)

-- 创建类型索引
CREATE INDEX IF NOT EXISTS idx_resources_type ON resources (type)

-- 创建 JSONB 属性索引（支持快速查询）
CREATE INDEX IF NOT EXISTS idx_resources_props ON resources USING GIN (props)

-- 示例数据
-- 资源点
INSERT INTO resources (type, geom, props) VALUES
('manhole', ST_SetSRID(ST_MakePoint(116.4074, 39.9042), 4326), '{"identifier": "MH-001", "status": "normal"}'),
('manhole', ST_SetSRID(ST_MakePoint(116.4174, 39.9042), 4326), '{"identifier": "MH-002", "status": "normal"}'),
('pole', ST_SetSRID(ST_MakePoint(116.4074, 39.9142), 4326), '{"identifier": "P-001", "status": "normal"}'),
('pole', ST_SetSRID(ST_MakePoint(116.4174, 39.9142), 4326), '{"identifier": "P-002", "status": "normal"}'),
('cabinet', ST_SetSRID(ST_MakePoint(116.4274, 39.9242), 4326), '{"identifier": "GH-202", "status": "normal"}'),
('office', ST_SetSRID(ST_MakePoint(116.4374, 39.9342), 4326), '{"identifier": "OFF-001", "name": "北京分公司"}');

-- 光缆段（使用LINESTRING）
INSERT INTO resources (type, geom, props) VALUES
('fiber_segment', ST_SetSRID(ST_MakeLine(ST_MakePoint(116.4074, 39.9042), ST_MakePoint(116.4174, 39.9042)), 4326), '{"identifier": "FS-001", "length": 1000, "status": "normal"}'),
('fiber_segment', ST_SetSRID(ST_MakeLine(ST_MakePoint(116.4174, 39.9042), ST_MakePoint(116.4174, 39.9142)), 4326), '{"identifier": "FS-002", "length": 1000, "status": "normal"}'),
('fiber_segment', ST_SetSRID(ST_MakeLine(ST_MakePoint(116.4174, 39.9142), ST_MakePoint(116.4074, 39.9142)), 4326), '{"identifier": "FS-003", "length": 1000, "status": "normal"}'),
('fiber_segment', ST_SetSRID(ST_MakeLine(ST_MakePoint(116.4074, 39.9142), ST_MakePoint(116.4074, 39.9042)), 4326), '{"identifier": "FS-004", "length": 1000, "status": "normal"}');

-- 查询示例
-- 1. 半径查询：查找某点 500 米内的所有人井
-- SELECT id, type, ST_X(geom) AS lng, ST_Y(geom) AS lat, props
-- FROM resources
-- WHERE type = 'manhole'
--   AND ST_DWithin(geom::geography, ST_SetSRID(ST_MakePoint(116.4074, 39.9042), 4326)::geography, 500);

-- 2. BBOX 查询：查找指定范围内的资源
-- SELECT id, type, ST_X(geom) AS lng, ST_Y(geom) AS lat, props
-- FROM resources
-- WHERE geom && ST_MakeEnvelope(116.4, 39.9, 116.5, 40.0, 4326);

-- 3. 属性查询：查找特定编号的资源
-- SELECT id, type, ST_AsText(geom), props
-- FROM resources
-- WHERE props->>'identifier' = 'GH-202';
