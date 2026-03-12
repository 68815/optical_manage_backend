package com.optical.manage.controller;

import com.optical.manage.dto.map.ResourceRequest;
import com.optical.manage.dto.map.FiberSegmentRequest;
import com.optical.manage.dto.map.MapQueryRequest;
import com.optical.manage.dto.map.MapResponse;
import com.optical.manage.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/map")
public class MapController {
    
    @Autowired
    private MapService mapService;
    
    // 资源点操作
    @PostMapping("/resource-point")
    public Map<String, Object> createResource(@RequestBody ResourceRequest request) {
        Long id = mapService.createResource(request);
        return Map.of("ok", true, "id", id);
    }
    
    @GetMapping("/resource-point/{id}")
    public Object getResource(@PathVariable Long id) {
        return mapService.getResource(id);
    }
    
    @PutMapping("/resources/{id}")
    public Map<String, Object> updateResource(@PathVariable Long id, @RequestBody ResourceRequest request) {
        boolean success = mapService.updateResource(id, request);
        return Map.of("ok", success);
    }
    
    @DeleteMapping("/resources/{id}")
    public Map<String, Object> deleteResource(@PathVariable Long id) {
        boolean success = mapService.deleteResource(id);
        return Map.of("ok", success);
    }
    
    // 光缆段操作
    @PostMapping("/fiber-segments")
    public Map<String, Object> createFiberSegment(@RequestBody FiberSegmentRequest request) {
        Long id = mapService.createFiberSegment(request);
        return Map.of("ok", true, "id", id);
    }
    
    @PutMapping("/fiber-segments/{id}")
    public Map<String, Object> updateFiberSegment(@PathVariable Long id, @RequestBody FiberSegmentRequest request) {
        boolean success = mapService.updateFiberSegment(id, request);
        return Map.of("ok", success);
    }
    
    // 查询功能
    @PostMapping("/query")
    public MapResponse queryResources(@RequestBody MapQueryRequest request) {
        return mapService.queryResources(request);
    }
    
    @PostMapping("/fiber-segments/query")
    public MapResponse getFiberSegments(@RequestBody MapQueryRequest request) {
        return mapService.getFiberSegments(request);
    }
}