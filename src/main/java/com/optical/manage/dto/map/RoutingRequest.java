package com.optical.manage.dto.map;

import lombok.Data;
import java.util.List;

@Data
public class RoutingRequest {
    private String name;
    private String type;
    private List<Point> points;
    private String props;
    
    @Data
    public static class Point {
        private Long id;
        private double lat;
        private double lng;
    }
}
