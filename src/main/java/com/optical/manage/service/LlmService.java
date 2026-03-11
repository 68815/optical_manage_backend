package com.optical.manage.service;

import com.optical.manage.dto.nlq.ParsedQuery;
import reactor.core.publisher.Mono;

public interface LlmService {
    
    Mono<ParsedQuery> parseQuery(String text, String context);
}
