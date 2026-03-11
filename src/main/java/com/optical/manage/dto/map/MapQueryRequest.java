package com.optical.manage.dto.map;

import lombok.Data;

@Data
public class MapQueryRequest {
    private String type;
    private Bbox bbox;
    private CenterRadius centerRadius;
    private String filter;
    private int limit = 100;
    
    @Data
    public static class Bbox {
        private double minLat;
        private double minLng;
        private double maxLat;
        private double maxLng;
    }
    
    @Data
    public static class CenterRadius {
        private double lat;
        private double lng;
        private double radiusM;
    }
}