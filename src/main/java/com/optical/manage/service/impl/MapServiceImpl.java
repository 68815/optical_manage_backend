package com.optical.manage.service.impl;

import com.optical.manage.dto.map.ResourceRequest;
import com.optical.manage.DO.Resource;
import com.optical.manage.dto.map.FiberSegmentRequest;
import com.optical.manage.dto.map.MapQueryRequest;
import com.optical.manage.dto.map.MapResponse;
import com.optical.manage.mapper.ResourceMapper;
import com.optical.manage.service.MapService;
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
    
    @Override
    public Long createResource(ResourceRequest request) {
        resourceMapper.insertResource(request.getType(), request.getLat(), request.getLng(), request.getProps());
        
        // 这里简化处理，实际应该返回生成的ID
        return 1L;
    }
    
    @Override
    public Resource getResource(Long id) {
        return resourceMapper.getById(id);
    }
    
    @Override
    public boolean updateResource(Long id, ResourceRequest request) {
        int result = resourceMapper.updateResource(id, request.getType(), request.getLat(), request.getLng(), request.getProps());
        return result > 0;
    }
    
    @Override
    public boolean deleteResource(Long id) {
        int result = resourceMapper.deleteById(id);
        return result > 0;
    }
    
    @Override
    public Long createFiberSegment(FiberSegmentRequest request) {
        List<Map<String, Double>> points = new ArrayList<>();
        for (FiberSegmentRequest.Point point : request.getPoints()) {
            Map<String, Double> pointMap = new HashMap<>();
            pointMap.put("lat", point.getLat());
            pointMap.put("lng", point.getLng());
            points.add(pointMap);
        }
        
        resourceMapper.insertFiberSegment(points, request.getProps());
        return 1L;
    }
    
    @Override
    public boolean updateFiberSegment(Long id, FiberSegmentRequest request) {
        List<Map<String, Double>> points = new ArrayList<>();
        for (FiberSegmentRequest.Point point : request.getPoints()) {
            Map<String, Double> pointMap = new HashMap<>();
            pointMap.put("lat", point.getLat());
            pointMap.put("lng", point.getLng());
            points.add(pointMap);
        }
        
        int result = resourceMapper.updateFiberSegment(id, points, request.getProps());
        return result > 0;
    }
    
    @Override
    public MapResponse queryResources(MapQueryRequest request) {
        MapResponse response = new MapResponse();
        List<MapResponse.ResourceInfo> resources = new ArrayList<>();
        
        try {
            List<String> resourceTypes = new ArrayList<>();
            if (request.getType() != null) {
                resourceTypes.add(request.getType());
            }
            
            // 这里简化处理，实际应该根据请求参数构建查询
            List<Map<String, Object>> result = resourceMapper.searchByFilters(resourceTypes, null, request.getLimit());
            
            for (Map<String, Object> item : result) {
                MapResponse.ResourceInfo resourceInfo = new MapResponse.ResourceInfo();
                resourceInfo.setId((Long) item.get("resources_id"));
                resourceInfo.setType((String) item.get("type"));
                resourceInfo.setLat((Double) item.get("lat"));
                resourceInfo.setLng((Double) item.get("lng"));
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
            Map<String, Double> bbox = null;
            if (request.getBbox() != null) {
                bbox = new HashMap<>();
                bbox.put("minLat", request.getBbox().getMinLat());
                bbox.put("minLng", request.getBbox().getMinLng());
                bbox.put("maxLat", request.getBbox().getMaxLat());
                bbox.put("maxLng", request.getBbox().getMaxLng());
            }
            
            List<Map<String, Object>> result = resourceMapper.getFiberSegments(bbox);
            
            for (Map<String, Object> item : result) {
                MapResponse.ResourceInfo resourceInfo = new MapResponse.ResourceInfo();
                resourceInfo.setId((Long) item.get("resources_id"));
                resourceInfo.setType((String) item.get("type"));
                // 对于线段，我们需要解析geom字段来获取坐标
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
}