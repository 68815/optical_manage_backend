package com.optical.manage.service;

import com.optical.manage.dto.map.ResourceRequest;
import com.optical.manage.DO.Resource;
import com.optical.manage.dto.map.FiberSegmentRequest;
import com.optical.manage.dto.map.MapQueryRequest;
import com.optical.manage.dto.map.MapResponse;

public interface MapService {
    // 资源点操作
    Long createResource(ResourceRequest request);
    Resource getResourcePoint(Long id);  
    boolean updateResource(Long id, ResourceRequest request);
    boolean deleteResource(Long id);
    
    // 光缆段操作
    Long createFiberSegment(FiberSegmentRequest request);
    boolean updateFiberSegment(Long id, FiberSegmentRequest request);
    
    // 查询功能
    MapResponse queryResources(MapQueryRequest request);
    MapResponse getFiberSegments(MapQueryRequest request);
}