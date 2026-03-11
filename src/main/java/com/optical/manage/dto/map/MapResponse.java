package com.optical.manage.dto.map;

import lombok.Data;
import java.util.List;

@Data
public class MapResponse {
    private boolean ok;
    private List<ResourceInfo> resources;
    private String message;
    
    @Data
    public static class ResourceInfo {
        private Long id;
        private String type;
        private double lat;
        private double lng;
        private String props;
    }
}