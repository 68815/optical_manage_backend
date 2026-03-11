package com.optical.manage.service;

import com.optical.manage.dto.nlq.NlqRequest;
import com.optical.manage.dto.nlq.NlqResponse;
import com.optical.manage.dto.nlq.ParsedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class NlqService {
    
    private final LlmService llmService;
    
    public Mono<NlqResponse> parse(NlqRequest request) {
        String context = buildContext(request);
        
        return llmService.parseQuery(request.getText(), context)
                .map(parsed -> validateAndRespond(parsed, request));
    }
    
    private String buildContext(NlqRequest request) {
        StringBuilder context = new StringBuilder();
        
        NlqRequest.ClientContext clientContext = request.getClientContext();
        if (clientContext != null) {
            if (clientContext.getBbox() != null && clientContext.getBbox().size() == 4) {
                context.append("当前地图范围 (bbox): [")
                        .append(clientContext.getBbox().get(0)).append(", ")
                        .append(clientContext.getBbox().get(1)).append(", ")
                        .append(clientContext.getBbox().get(2)).append(", ")
                        .append(clientContext.getBbox().get(3)).append("] ");
            }
            
            if (clientContext.getCenter() != null) {
                context.append("当前中心点：lat=")
                        .append(clientContext.getCenter().getLat())
                        .append(", lng=")
                        .append(clientContext.getCenter().getLng())
                        .append(" ");
            }
            
            if (clientContext.getZoom() != null) {
                context.append("缩放级别：").append(clientContext.getZoom()).append(" ");
            }
        }
        
        if (request.getMaxResults() != null) {
            context.append("最大结果数：").append(request.getMaxResults());
        }
        
        return context.toString();
    }
    
    private NlqResponse validateAndRespond(ParsedQuery parsed, NlqRequest request) {
        if (parsed.getGeo() == null && request.getClientContext() == null) {
            return NlqResponse.needsConfirmation(
                    parsed,
                    "您的查询缺少地理位置信息，是否使用当前地图范围进行搜索？"
            );
        }
        
        if (parsed.getGeo() != null && "radius".equals(parsed.getGeo().getType()) 
                && parsed.getGeo().getCenter() == null) {
            if (request.getClientContext() != null && request.getClientContext().getCenter() != null) {
                parsed.getGeo().setCenter(convertCenter(request.getClientContext().getCenter()));
            } else {
                return NlqResponse.needsConfirmation(
                        parsed,
                        "您想查询\"附近的\"资源，但未提供当前位置，是否使用当前地图中心点？"
                );
            }
        }
        
        String sql = generateSqlExample(parsed);
        return NlqResponse.success(parsed);
    }
    
    private ParsedQuery.GeoConstraint.Center convertCenter(NlqRequest.ClientContext.Center center) {
        ParsedQuery.GeoConstraint.Center converted = new ParsedQuery.GeoConstraint.Center();
        converted.setLat(center.getLat());
        converted.setLng(center.getLng());
        return converted;
    }
    
    private String generateSqlExample(ParsedQuery parsed) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT id, type, ST_AsText(geom) AS geom, props FROM resources WHERE 1=1");
        
        if (parsed.getResourceTypes() != null && !parsed.getResourceTypes().isEmpty()) {
            sql.append(" AND type IN (");
            for (int i = 0; i < parsed.getResourceTypes().size(); i++) {
                if (i > 0) sql.append(", ");
                sql.append("'").append(parsed.getResourceTypes().get(i)).append("'");
            }
            sql.append(")");
        }
        
        if (parsed.getGeo() != null) {
            if ("radius".equals(parsed.getGeo().getType())) {
                sql.append(" AND ST_DWithin(geom::geography, ST_SetSRID(ST_MakePoint(")
                        .append(parsed.getGeo().getCenter().getLng()).append(", ")
                        .append(parsed.getGeo().getCenter().getLat()).append("), 4326)::geography, ")
                        .append(parsed.getGeo().getRadiusM()).append(")");
            } else if ("bbox".equals(parsed.getGeo().getType())) {
                sql.append(" AND geom && ST_MakeEnvelope(")
                        .append(parsed.getGeo().getMinLng()).append(", ")
                        .append(parsed.getGeo().getMinLat()).append(", ")
                        .append(parsed.getGeo().getMaxLng()).append(", ")
                        .append(parsed.getGeo().getMaxLat()).append(", 4326)");
            }
        }
        
        if (parsed.getFilters() != null) {
            parsed.getFilters().forEach((key, value) -> {
                if (value instanceof String) {
                    sql.append(" AND props->>'").append(key).append("' = '").append(value).append("'");
                } else {
                    sql.append(" AND props->>'").append(key).append("' = '").append(value.toString()).append("'");
                }
            });
        }
        
        sql.append(" LIMIT ").append(parsed.getLimit() != null ? parsed.getLimit() : 100);
        
        return sql.toString();
    }
}
