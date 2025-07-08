package com.example.user_service.config;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class UserContextFilter implements Filter {

    // 用于在当前线程中存储用户信息的上下文
    private static final ThreadLocal<UserContext> userContextHolder = new ThreadLocal<>();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String username = decodeHeader(request.getHeader("X-Authenticated-User"));
        String rolesHeader = decodeHeader(request.getHeader("X-Authenticated-Roles"));
        
        List<String> roles = (rolesHeader != null && !rolesHeader.isEmpty())
                             ? Arrays.asList(rolesHeader.split(","))
                             : Collections.emptyList();

        UserContext context = new UserContext(username, roles);
        userContextHolder.set(context);

        try {
            // 继续执行后续的过滤器和Controller
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            // 请求处理完毕后，清理ThreadLocal，防止内存泄漏
            userContextHolder.remove();
        }
    }

    public static UserContext getCurrentUser() {
        return userContextHolder.get();
    }
    
    private String decodeHeader(String value) {
        if (value == null) return null;
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    // 一个简单的内部类，用于存储上下文信息
    public static class UserContext {
        private final String username;
        private final List<String> roles;

        public UserContext(String username, List<String> roles) {
            this.username = username;
            this.roles = roles;
        }

        public String getUsername() { return username; }
        public List<String> getRoles() { return roles; }
        public boolean hasRole(String role) {
            return roles != null && roles.contains(role);
        }
    }
}