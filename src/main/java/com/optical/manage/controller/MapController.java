package com.optical.manage.controller;

import com.optical.manage.dto.map.ResourceRequest;
import com.optical.manage.dto.map.FiberSegmentRequest;
import com.optical.manage.dto.map.MapQueryRequest;
import com.optical.manage.dto.map.MapResponse;
import com.optical.manage.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import com.optical.manage.DO.Resource;

@RestController
@RequestMapping("/api/v1/map")
public class MapController {
    
    @Autowired
    private MapService mapService;
    
    // 资源点操作
    @PostMapping("/resource-point")
    public Mono<ResponseEntity<Long>> createResource(@RequestBody ResourceRequest request) {
        Long id = mapService.createResource(request);
        if(null == id || id <= 0) {
            return Mono.just(ResponseEntity.badRequest().body(id));
        }
        return Mono.just(ResponseEntity.ok(id));
    }
    
    @GetMapping("/resource-point/{id}")
    public Mono<ResponseEntity<Resource>> getResourcePoint(@PathVariable Long id) {
        Resource resource = mapService.getResourcePoint(id);
        if(null == resource) {
            return Mono.just(ResponseEntity.notFound().build());
        }
        return Mono.just(ResponseEntity.ok(resource));
    }
    
    @PutMapping("/resource-point/{id}")
    public Mono<ResponseEntity<Map<String, Object>>> updateResource(@PathVariable Long id, @RequestBody ResourceRequest request) {
        boolean success = mapService.updateResource(id, request);
        return Mono.just(ResponseEntity.ok(Map.of("ok", success)));
    }
    
    @DeleteMapping("/resource-point/{id}")
    public Mono<ResponseEntity<Map<String, Object>>> deleteResource(@PathVariable Long id) {
        boolean success = mapService.deleteResource(id);
        return Mono.just(ResponseEntity.ok(Map.of("ok", success)));
    }
    
    // 光缆段操作
    @PostMapping("/fiber-segments")
    public Mono<Map<String, Object>> createFiberSegment(@RequestBody FiberSegmentRequest request) {
        Long id = mapService.createFiberSegment(request);
        return Mono.just(Map.of("ok", true, "id", id));
    }
    
    @PutMapping("/fiber-segments/{id}")
    public Mono<Map<String, Object>> updateFiberSegment(@PathVariable Long id, @RequestBody FiberSegmentRequest request) {
        boolean success = mapService.updateFiberSegment(id, request);
        return Mono.just(Map.of("ok", success));
    }
    
    // 查询功能
    @PostMapping("/query")
    public Mono<MapResponse> queryResources(@RequestBody MapQueryRequest request) {
        return Mono.just(mapService.queryResources(request));
    }
    
    @PostMapping("/fiber-segments/query")
    public Mono<MapResponse> getFiberSegments(@RequestBody MapQueryRequest request) {
        return Mono.just(mapService.getFiberSegments(request));
    }
}