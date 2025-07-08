package com.example.gateway_service.utils; 


import org.springframework.http.HttpHeaders;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import reactor.core.publisher.Mono;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.security.core.context.SecurityContext; // 【修正】导入正确的SecurityContext类
import org.springframework.security.core.context.SecurityContextImpl; // 【修正】我们需要它来创建实例
import org.springframework.stereotype.Component; // 【关键】

/**
 * 【新增/重构】一个更符合WebFlux和Spring Security 6风格的JWT处理器
 * 它实现了ServerSecurityContextRepository接口，负责从请求中加载安全上下文，以及保存安全上下文。
 */

@Component // 【关键】reactor.core.publisher.Mono;
public class JwtSecurityContextRepository implements ServerSecurityContextRepository {

    private final JwtUtil jwtUtil;

    public JwtSecurityContextRepository(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext  context) {
        // 因为我们是无状态的JWT，所以不需要实现保存上下文的逻辑
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext > load(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String authToken = authHeader.substring(7);
            if (jwtUtil.validateToken(authToken)) {
                String username = jwtUtil.getUsernameFromToken(authToken);
                var authorities = jwtUtil.getRolesFromToken(authToken);
                Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                                // 【修正】创建一个新的SecurityContext实例，并将Authentication对象放进去
                SecurityContext context = new SecurityContextImpl(authentication);
                // 将这个包含认证信息的上下文，通过ReactiveSecurityContextHolder返回
                // .defer() 用于延迟执行，确保在订阅时才创建上下文
                return Mono.just(context);
            }
        }
        // 如果没有Token或Token无效，返回一个空的安全上下文
        return Mono.empty();
    }
}