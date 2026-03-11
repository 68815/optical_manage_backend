package com.optical.manage.dto.nlq;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NlqResponse {
    
    private boolean ok;
    
    private ParsedQuery parsed;
    
    @JsonProperty("candidates_sql")
    private String candidatesSql;
    
    private String notes;
    
    @JsonProperty("needs_confirmation")
    private Boolean needsConfirmation;
    
    private String message;
    
    private String error;
    
    public static NlqResponse success(ParsedQuery parsed) {
        return NlqResponse.builder()
                .ok(true)
                .parsed(parsed)
                .build();
    }
    
    public static NlqResponse needsConfirmation(ParsedQuery parsed, String message) {
        return NlqResponse.builder()
                .ok(true)
                .parsed(parsed)
                .needsConfirmation(true)
                .message(message)
                .build();
    }
    
    public static NlqResponse error(String error, String message) {
        return NlqResponse.builder()
                .ok(false)
                .error(error)
                .message(message)
                .build();
    }
}
