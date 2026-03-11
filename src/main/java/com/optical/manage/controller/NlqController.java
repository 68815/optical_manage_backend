package com.optical.manage.controller;

import com.optical.manage.dto.nlq.NlqRequest;
import com.optical.manage.dto.nlq.NlqResponse;
import com.optical.manage.service.NlqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/nlq")
@RequiredArgsConstructor
public class NlqController {
    
    private final NlqService nlqService;
    
    @PostMapping("/parse")
    public Mono<ResponseEntity<NlqResponse>> parseQuery(@RequestBody NlqRequest request) {
        log.info("收到 NLQ 请求：{}", request.getText());
        
        return nlqService.parse(request)
                .map(ResponseEntity::ok)
                .onErrorResume(error -> {
                    log.error("NLQ 解析失败", error);
                    return Mono.just(ResponseEntity.ok(
                            NlqResponse.error("parse_failed", "无法理解查询：" + error.getMessage())));
                });
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("NLQ service is running");
    }
}
