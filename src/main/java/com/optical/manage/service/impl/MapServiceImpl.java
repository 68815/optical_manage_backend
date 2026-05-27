package com.optical.manage.service.impl;

import com.optical.manage.dto.map.ResourceRequest;
import com.optical.manage.dto.map.RoutingRequest;
import com.optical.manage.DO.Resource;
import com.optical.manage.DO.Routing;
import com.optical.manage.DO.FiberSegment;
import com.optical.manage.DO.Cable;
import com.optical.manage.DO.CableRepeater;
import com.optical.manage.dto.map.FiberSegmentRequest;
import com.optical.manage.dto.map.MapQueryRequest;
import com.optical.manage.dto.map.MapResponse;
import com.optical.manage.mapper.ResourceMapper;
import com.optical.manage.mapper.RoutingMapper;
import com.optical.manage.mapper.FiberSegmentMapper;
import com.optical.manage.mapper.CableMapper;
import com.optical.manage.mapper.CableRepeaterMapper;
import com.optical.manage.service.MapService;
import com.optical.manage.util.CoordinateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class MapServiceImpl implements MapService {

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private RoutingMapper routingMapper;

    @Autowired
    private FiberSegmentMapper fiberSegmentMapper;

    @Autowired
    private CableMapper cableMapper;

    @Autowired
    private CableRepeaterMapper cableRepeaterMapper;

    // ========== 资源点操作 ==========

    @Override
    public Long createResource(ResourceRequest request) {
        Resource resource = new Resource();
        resource.setType(request.getType());
        resource.setName(request.getName());
        resource.setAddress(request.getAddress());
        resource.setStatus(request.getStatus() != null ? request.getStatus() : 0);
        // GCJ-02 转 WGS84
        double[] wgs84 = CoordinateConverter.gcj02ToWgs84(request.getLat(), request.getLng());
        String wkt = String.format("POINT(%f %f)", wgs84[1], wgs84[0]);
        resource.setGeom(wkt);
        resource.setProps(request.getProps());
        resourceMapper.insertResource(resource);
        return resource.getResourcePointId();
    }

    @Override
    public Resource getResourcePoint(Long id) {
        return resourceMapper.selectById(id);
    }

    @Override
    public boolean updateResource(Long id, ResourceRequest request) {
        Resource resource = new Resource();
        resource.setResourcePointId(id);
        resource.setName(request.getName());
        resource.setType(request.getType());
        resource.setAddress(request.getAddress());
        resource.setStatus(request.getStatus() != null ? request.getStatus() : 0);
        // GCJ-02 转 WGS84
        double[] wgs84 = CoordinateConverter.gcj02ToWgs84(request.getLat(), request.getLng());
        String wkt = String.format("POINT(%f %f)", wgs84[1], wgs84[0]);
        resource.setGeom(wkt);
        resource.setProps(request.getProps());
        return resourceMapper.updateResource(resource) > 0;
    }

    @Override
    public boolean deleteResource(Long id) {
        return resourceMapper.deleteResourceById(id) > 0;
    }

    // ========== 路由段操作 ==========

    @Override
    public Long createRouting(RoutingRequest request) {
        Routing routing = new Routing();
        routing.setName(request.getName());
        routing.setType(request.getType());
        routing.setCablesCount(0);

        if (request.getPoints() != null && request.getPoints().size() >= 2) {
            RoutingRequest.Point startPoint = request.getPoints().get(0);
            RoutingRequest.Point endPoint = request.getPoints().get(request.getPoints().size() - 1);
            routing.setStartPointId(startPoint.getId() != null ? startPoint.getId() : 1L);
            routing.setEndPointId(endPoint.getId() != null ? endPoint.getId() : 2L);

            StringBuilder wkt = new StringBuilder("LINESTRING(");
            for (int i = 0; i < request.getPoints().size(); i++) {
                RoutingRequest.Point point = request.getPoints().get(i);
                // GCJ-02 转 WGS84
                double[] wgs84 = CoordinateConverter.gcj02ToWgs84(point.getLat(), point.getLng());
                if (i > 0) wkt.append(", ");
                wkt.append(wgs84[1]).append(" ").append(wgs84[0]);
            }
            wkt.append(")");
            routing.setGeom(wkt.toString());
        }

        routing.setProps(request.getProps());
        routingMapper.insertRouting(routing);
        return routing.getRoutingId();
    }

    @Override
    public Routing getRouting(Long id) {
        return routingMapper.selectById(id);
    }

    @Override
    public boolean updateRouting(Long id, RoutingRequest request) {
        Routing routing = new Routing();
        routing.setRoutingId(id);
        routing.setName(request.getName());
        routing.setType(request.getType());

        if (request.getPoints() != null && request.getPoints().size() >= 2) {
            RoutingRequest.Point startPoint = request.getPoints().get(0);
            RoutingRequest.Point endPoint = request.getPoints().get(request.getPoints().size() - 1);

            routing.setStartPointId(startPoint.getId() != null ? startPoint.getId() : 1L);
            routing.setEndPointId(endPoint.getId() != null ? endPoint.getId() : 2L);

            StringBuilder wkt = new StringBuilder("LINESTRING(");
            for (int i = 0; i < request.getPoints().size(); i++) {
                RoutingRequest.Point point = request.getPoints().get(i);
                // GCJ-02 转 WGS84
                double[] wgs84 = CoordinateConverter.gcj02ToWgs84(point.getLat(), point.getLng());
                if (i > 0) wkt.append(", ");
                wkt.append(wgs84[1]).append(" ").append(wgs84[0]);
            }
            wkt.append(")");
            routing.setGeom(wkt.toString());
        }

        routing.setProps(request.getProps());
        return routingMapper.updateRouting(routing) > 0;
    }

    @Override
    public boolean deleteRouting(Long id) {
        return routingMapper.deleteById(id) > 0;
    }

    // ========== 光缆段操作 ==========

    @Override
    public Long createFiberSegment(FiberSegmentRequest request) {
        FiberSegment fiberSegment = new FiberSegment();
        fiberSegment.setName(request.getName());
        fiberSegment.setRoutingId(request.getRoutingId());
        fiberSegment.setCableLevel(request.getCableLevel());
        fiberSegment.setLength(request.getLength());
        fiberSegment.setFiberCount(request.getFiberCount());
        fiberSegment.setTubeCount(request.getTubeCount());
        fiberSegment.setFibersPerTube(request.getFibersPerTube());
        fiberSegment.setLayingStyle(request.getLayingStyle());

        if (request.getPoints() != null && request.getPoints().size() >= 2) {
            FiberSegmentRequest.Point startPoint = request.getPoints().get(0);
            FiberSegmentRequest.Point endPoint = request.getPoints().get(request.getPoints().size() - 1);

            fiberSegment.setStartPointId(startPoint.getId() != null ? startPoint.getId() : 1L);
            fiberSegment.setEndPointId(endPoint.getId() != null ? endPoint.getId() : 2L);

            StringBuilder wkt = new StringBuilder("LINESTRING(");
            for (int i = 0; i < request.getPoints().size(); i++) {
                FiberSegmentRequest.Point point = request.getPoints().get(i);
                // GCJ-02 转 WGS84
                double[] wgs84 = CoordinateConverter.gcj02ToWgs84(point.getLat(), point.getLng());
                if (i > 0) wkt.append(", ");
                wkt.append(wgs84[1]).append(" ").append(wgs84[0]);
            }
            wkt.append(")");
            fiberSegment.setGeom(wkt.toString());
        }

        fiberSegment.setProps(request.getProps());
        fiberSegmentMapper.insertSegment(fiberSegment);
        return fiberSegment.getSegmentId();
    }

    @Override
    public boolean updateFiberSegment(Long id, FiberSegmentRequest request) {
        FiberSegment fiberSegment = new FiberSegment();
        fiberSegment.setSegmentId(id);
        fiberSegment.setName(request.getName());
        fiberSegment.setRoutingId(request.getRoutingId());
        fiberSegment.setCableLevel(request.getCableLevel());
        fiberSegment.setLength(request.getLength());
        fiberSegment.setFiberCount(request.getFiberCount());
        fiberSegment.setTubeCount(request.getTubeCount());
        fiberSegment.setFibersPerTube(request.getFibersPerTube());
        fiberSegment.setLayingStyle(request.getLayingStyle());

        if (request.getPoints() != null && request.getPoints().size() >= 2) {
            FiberSegmentRequest.Point startPoint = request.getPoints().get(0);
            FiberSegmentRequest.Point endPoint = request.getPoints().get(request.getPoints().size() - 1);

            fiberSegment.setStartPointId(startPoint.getId() != null ? startPoint.getId() : 1L);
            fiberSegment.setEndPointId(endPoint.getId() != null ? endPoint.getId() : 2L);

            StringBuilder wkt = new StringBuilder("LINESTRING(");
            for (int i = 0; i < request.getPoints().size(); i++) {
                FiberSegmentRequest.Point point = request.getPoints().get(i);
                // GCJ-02 转 WGS84
                double[] wgs84 = CoordinateConverter.gcj02ToWgs84(point.getLat(), point.getLng());
                if (i > 0) wkt.append(", ");
                wkt.append(wgs84[1]).append(" ").append(wgs84[0]);
            }
            wkt.append(")");
            fiberSegment.setGeom(wkt.toString());
        }

        fiberSegment.setProps(request.getProps());
        return fiberSegmentMapper.updateSegment(fiberSegment) > 0;
    }

    @Override
    public boolean deleteFiberSegment(Long id) {
        return fiberSegmentMapper.deleteById(id) > 0;
    }

    // ========== 全程缆操作 ==========

    public Long createCable(Cable cable) {
        cableMapper.insertCable(cable);
        return cable.getCableId();
    }

    public Cable getCable(Long id) {
        return cableMapper.selectById(id);
    }

    public boolean updateCable(Cable cable) {
        return cableMapper.updateCable(cable) > 0;
    }

    public boolean deleteCable(Long id) {
        return cableMapper.deleteById(id) > 0;
    }

    public List<Cable> getAllCables() {
        return cableMapper.selectAll();
    }

    // ========== 中继段操作 ==========

    public Long createCableRepeater(CableRepeater repeater) {
        cableRepeaterMapper.insertRepeater(repeater);
        return repeater.getRepeaterId();
    }

    public CableRepeater getCableRepeater(Long id) {
        return cableRepeaterMapper.selectById(id);
    }

    public boolean updateCableRepeater(CableRepeater repeater) {
        return cableRepeaterMapper.updateRepeater(repeater) > 0;
    }

    public boolean deleteCableRepeater(Long id) {
        return cableRepeaterMapper.deleteById(id) > 0;
    }

    public List<CableRepeater> getAllCableRepeaters() {
        return cableRepeaterMapper.selectAll();
    }

    // ========== 查询功能 ==========

    @Override
    public MapResponse queryResources(MapQueryRequest request) {
        MapResponse response = new MapResponse();
        List<MapResponse.ResourceInfo> resources = new ArrayList<>();

        try {
            List<String> resourceTypes = new ArrayList<>();
            // 如果 type 为 resource 或 null，查询所有资源类型
            if (request.getType() != null && !"resource".equals(request.getType())) {
                resourceTypes.add(request.getType());
            }

            Map<String, Object> filters = null;
            String nameFilter = null;
            if (request.getFilter() != null && !request.getFilter().isEmpty()) {
                filters = new HashMap<>();
                String[] filterPairs = request.getFilter().split(",");
                for (String pair : filterPairs) {
                    String[] kv = pair.split("=");
                    if (kv.length == 2) {
                        String key = kv[0].trim();
                        String value = kv[1].trim();
                        if ("name".equals(key)) {
                            nameFilter = value;
                        } else {
                            filters.put(key, value);
                        }
                    }
                }
                if (filters.isEmpty()) filters = null;
            }

            Double minLng = null, minLat = null, maxLng = null, maxLat = null;
            if (request.getBbox() != null) {
                minLng = request.getBbox().getMinLng();
                minLat = request.getBbox().getMinLat();
                maxLng = request.getBbox().getMaxLng();
                maxLat = request.getBbox().getMaxLat();
            }

            Double centerLng = null, centerLat = null, radiusM = null;
            if (request.getCenterRadius() != null) {
                centerLng = request.getCenterRadius().getLng();
                centerLat = request.getCenterRadius().getLat();
                radiusM = request.getCenterRadius().getRadiusM();
            }

            List<Map<String, Object>> result = resourceMapper.searchByFilters(
                    resourceTypes.isEmpty() ? null : resourceTypes,
                    filters, nameFilter, minLng, minLat, maxLng, maxLat,
                    centerLng, centerLat, radiusM, request.getLimit());

            for (Map<String, Object> item : result) {
                MapResponse.ResourceInfo resourceInfo = new MapResponse.ResourceInfo();
                resourceInfo.setId(((Number) item.get("resource_point_id")).longValue());
                resourceInfo.setType((String) item.get("type"));
                resourceInfo.setName((String) item.get("name"));
                // 直接返回 WGS84 坐标，前端自行转换
                Double lat = item.get("lat") != null ? ((Number) item.get("lat")).doubleValue() : null;
                Double lng = item.get("lng") != null ? ((Number) item.get("lng")).doubleValue() : null;
                resourceInfo.setLat(lat != null ? lat : 0.0);
                resourceInfo.setLng(lng != null ? lng : 0.0);
                resourceInfo.setProps((String) item.get("props"));
                resources.add(resourceInfo);
            }

            response.setOk(true);
            response.setResources(resources);
        } catch (Exception e) {
            response.setOk(false);
            response.setMessage("查询失败: " + e.getMessage());
        }

        return response;
    }

    @Override
    public MapResponse getFiberSegments(MapQueryRequest request) {
        MapResponse response = new MapResponse();
        List<MapResponse.ResourceInfo> resources = new ArrayList<>();

        try {
            Double minLng = null, minLat = null, maxLng = null, maxLat = null;
            if (request.getBbox() != null) {
                minLng = request.getBbox().getMinLng();
                minLat = request.getBbox().getMinLat();
                maxLng = request.getBbox().getMaxLng();
                maxLat = request.getBbox().getMaxLat();
            }

            Double centerLng = null, centerLat = null, radiusM = null;
            if (request.getCenterRadius() != null) {
                centerLng = request.getCenterRadius().getLng();
                centerLat = request.getCenterRadius().getLat();
                radiusM = request.getCenterRadius().getRadiusM();
            }

            List<FiberSegment> segments = fiberSegmentMapper.selectByBbox(
                    minLng, minLat, maxLng, maxLat,
                    centerLng, centerLat, radiusM, request.getLimit());

            for (FiberSegment segment : segments) {
                MapResponse.ResourceInfo resourceInfo = new MapResponse.ResourceInfo();
                resourceInfo.setId(segment.getSegmentId());
                resourceInfo.setName(segment.getName());
                resourceInfo.setType("fiber_segment");
                // 直接返回 WGS84 格式的 WKT，前端自行转换
                resourceInfo.setGeom(segment.getGeom());
                resourceInfo.setProps(segment.getProps());
                resources.add(resourceInfo);
            }

            response.setOk(true);
            response.setResources(resources);
        } catch (Exception e) {
            response.setOk(false);
            response.setMessage("查询失败: " + e.getMessage());
        }

        return response;
    }

    @Override
    public MapResponse getRoutings(MapQueryRequest request) {
        MapResponse response = new MapResponse();
        List<MapResponse.ResourceInfo> resources = new ArrayList<>();

        try {
            Double minLng = null, minLat = null, maxLng = null, maxLat = null;
            if (request.getBbox() != null) {
                minLng = request.getBbox().getMinLng();
                minLat = request.getBbox().getMinLat();
                maxLng = request.getBbox().getMaxLng();
                maxLat = request.getBbox().getMaxLat();
            }

            List<Routing> routings = routingMapper.selectByBbox(
                    minLng, minLat, maxLng, maxLat, request.getLimit());

            for (Routing routing : routings) {
                MapResponse.ResourceInfo resourceInfo = new MapResponse.ResourceInfo();
                resourceInfo.setId(routing.getRoutingId());
                resourceInfo.setName(routing.getName());
                resourceInfo.setType("routing");
                // 直接返回 WGS84 格式的 WKT，前端自行转换
                resourceInfo.setGeom(routing.getGeom());
                resourceInfo.setProps(routing.getProps());
                resources.add(resourceInfo);
            }

            response.setOk(true);
            response.setResources(resources);
        } catch (Exception e) {
            response.setOk(false);
            response.setMessage("查询失败: " + e.getMessage());
        }

        return response;
    }

}
