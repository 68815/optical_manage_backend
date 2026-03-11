package com.optical.manage.dto.map;

import lombok.Data;
import java.util.List;

@Data
public class FiberSegmentRequest {
    private List<Point> points;
    private String props;
    
    @Data
    public static class Point {
        private double lat;
        private double lng;
    }
}