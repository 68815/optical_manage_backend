package com.optical.manage.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optical.manage.config.LlmProperties;
import com.optical.manage.dto.nlq.ParsedQuery;
import com.optical.manage.service.LlmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmServiceImpl implements LlmService {
        
    private final LlmProperties llmProperties;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    @Override
    public Mono<ParsedQuery> parseQuery(String text, String context) {
        
        String userPrompt = buildUserPrompt(text, context);
        
        return webClient.post()
                .uri(llmProperties.getApiUrl())
                .header("Authorization", "Bearer " + llmProperties.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(buildRequestBody(userPrompt))
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extractJsonFromResponse)
                .map(this::parseToJson)
                .doOnSuccess(parsed -> log.info("LLM 解析成功：{}", parsed.getIntent()))
                .doOnError(error -> log.error("LLM 解析失败", error));
    }
    
    
    private String buildUserPrompt(String text, String context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("用户查询：").append(text);
        
        if (context != null && !context.isEmpty()) {
            prompt.append("\n当前上下文：").append(context);
        }
        
        return prompt.toString();
    }
    
    private Map<String, Object> buildRequestBody(String userPrompt) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", llmProperties.getModel());
        body.put("temperature", llmProperties.getTemperature());
        body.put("max_tokens", llmProperties.getMaxTokens());
        
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", getSystemPrompt());
        
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userPrompt);
        
        body.put("messages", List.of(systemMessage, userMessage));
        
        return body;
    }
    
    private String getSystemPrompt() {
        return """
        你是一个将中文地图搜索查询转换为结构化查询的解析器。只输出严格的 JSON（不带解释文字）。JSON 必须遵守下面的 schema 并使用中文/英文键名字段。若字段无法解析，请使用 null 或空数组。确保地理约束用 bbox 或 center+radius 的形式。不要输出额外文本。
        
        示例 1:
        用户："显示我附近 500 米内的所有人井"
        解析：{"intent":"search_resources","resource_types":["manhole"],"filters":{},"geo":{"type":"radius","center":{"lat":39.12345,"lng":116.12345},"radius_m":500},"limit":100,"human_readable":"在当前位置 500m 内查找人井","confidence":0.92}
        
        示例 2:
        用户："列出北京市海淀区所有光缆段"
        解析：{"intent":"search_cables","resource_types":["fiber_segment"],"filters":{"administrative_area":"海淀区","city":"北京市"},"geo":{"type":"bbox","minLat":39.9,"minLng":116.1,"maxLat":40.02,"maxLng":116.4},"limit":200,"human_readable":"查询北京市海淀区的所有光缆段","confidence":0.95}
        
        示例 3:
        用户："显示编号为 GH-202 的接头箱"
        解析：{"intent":"search_resources","resource_types":["cabinet"],"filters":{"identifier":"GH-202"},"geo":null,"limit":10,"human_readable":"查找编号为 GH-202 的接头箱","confidence":0.98}
        
        示例 4:
        用户："最近 7 天故障的所有电杆"
        解析：{"intent":"search_resources","resource_types":["pole"],"filters":{"status":"fault","date_from":"2026-02-27"},"geo":null,"limit":100,"human_readable":"查询最近 7 天内故障的电杆","confidence":0.90}
        
        示例 5:
        用户："朝阳区有多少个交接箱"
        解析：{"intent":"count","resource_types":["cabinet"],"filters":{"administrative_area":"朝阳区"},"geo":null,"limit":1,"human_readable":"统计朝阳区交接箱的数量","confidence":0.93}
        """;
    }
    
    private String extractJsonFromResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode content = choices.get(0).get("message").get("content");
                if (content != null) {
                    return content.asText().trim();
                }
            }
            throw new RuntimeException("无法从响应中提取内容");
        } catch (JsonProcessingException e) {
            log.error("解析 LLM 响应失败", e);
            throw new RuntimeException("解析 LLM 响应失败", e);
        }
    }
    
    private ParsedQuery parseToJson(String jsonContent) {
        try {
            return objectMapper.readValue(jsonContent, ParsedQuery.class);
        } catch (JsonProcessingException e) {
            log.error("解析 ParsedQuery 失败：{}", jsonContent, e);
            throw new RuntimeException("解析 ParsedQuery 失败", e);
        }
    }
}
