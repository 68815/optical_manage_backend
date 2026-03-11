package com.optical.manage.dto.nlq;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NlqRequest {
    
    private String text;
    
    @JsonProperty("client_context")
    private ClientContext clientContext;
    
    @JsonProperty("max_results")
    private Integer maxResults;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClientContext {
        private List<Double> bbox;
        private Center center;
        private Integer zoom;
        
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Center {
            private Double lat;
            private Double lng;
        }
    }
}
