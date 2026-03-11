package com.optical.manage.dto.nlq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParsedQuery {
    
    private String intent;
    
    @JsonProperty("resource_types")
    private List<String> resourceTypes;
    
    private Map<String, Object> filters;
    
    private GeoConstraint geo;
    
    private Integer limit;
    
    @JsonProperty("human_readable")
    private String humanReadable;
    
    private Double confidence;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GeoConstraint {
        private String type;
        private Center center;
        
        @JsonProperty("radius_m")
        private Integer radiusM;
        
        @JsonProperty("minLat")
        private Double minLat;
        
        @JsonProperty("minLng")
        private Double minLng;
        
        @JsonProperty("maxLat")
        private Double maxLat;
        
        @JsonProperty("maxLng")
        private Double maxLng;
        
        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Center {
            private Double lat;
            private Double lng;
        }
    }
}
