package com.example.gateway_service.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 从响应式安全上下文中获取当前认证的用户信息
        return ReactiveSecurityContextHolder.getContext()
            .map(securityContext -> securityContext.getAuthentication())
            .flatMap(authentication -> {
                if (authentication != null && authentication.isAuthenticated()) {
                    // 获取用户名
                    String username = authentication.getName();
                    // 获取角色，并转换成逗号分隔的字符串
                    List<String> roles = authentication.getAuthorities().stream()
                            .map(grantedAuthority -> grantedAuthority.getAuthority())
                            .toList();
                    String rolesString = String.join(",", roles);

                    // 将用户信息添加到请求头中，传递给下游服务
                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                        .header("X-Authenticated-User", encodeHeader(username))
                        .header("X-Authenticated-Roles", encodeHeader(rolesString))
                        .build();
                    
                    ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
                    return chain.filter(mutatedExchange);
                }
                // 如果没有认证信息，直接放行（让下游的安全机制，如果有的话，来处理）
                return chain.filter(exchange);
            })
            // 如果上下文中没有任何认证信息，也直接放行
            .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        // 设置过滤器的优先级，-1表示较高优先级
        return -1;
    }

    // 简单的URL编码，防止中文等特殊字符在Header中出现问题
    private String encodeHeader(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}