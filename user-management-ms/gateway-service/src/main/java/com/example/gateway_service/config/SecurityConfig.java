package com.example.gateway_service.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpMethod;
import com.example.gateway_service.utils.JwtSecurityContextRepository;


@Configuration
@EnableWebFluxSecurity // 【注意】在WebFlux项目(Gateway基于它)中，要用这个注解
@EnableReactiveMethodSecurity // 【修正】使用@EnableReactiveMethodSecurity
public class SecurityConfig {
    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${server.user.name}")
    private String userName;

    @Value("${server.user.password}")
    private String password;

    @Autowired
    private JwtSecurityContextRepository jwtSecurityContextRepository; // 【关键】直接注入

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 【注意】返回的是 MapReactiveUserDetailsService，用于响应式环境
    @Bean
    public MapReactiveUserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
                .username(userName)
                .password(passwordEncoder.encode(password))
                .roles("ADMIN", "USER")
                .build();
        return new MapReactiveUserDetailsService(admin);
    }

        /**
     * 【已修正】WebFlux环境下的安全规则链配置
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // 禁用CSRF
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 【推荐】直接在这里配置CORS
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) // 禁用HttpBasic
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable) // 禁用表单登录
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/auth/login").permitAll() // 登录接口公开
                        .pathMatchers(HttpMethod.OPTIONS).permitAll() // 预检请求公开
                        .anyExchange().authenticated() // 其他所有请求都需要认证
                )
                .securityContextRepository(jwtSecurityContextRepository)
                .build();
    }

    // // 【注意】返回的是 SecurityWebFilterChain，参数是 ServerHttpSecurity
    // @Bean
    // public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    //     http
    //         .authorizeExchange(exchange -> exchange
    //             // 预检请求(CORS preflight)直接放行
    //             .pathMatchers(org.springframework.http.HttpMethod.OPTIONS).permitAll()
    //             // 其他所有请求都需要认证
    //             .anyExchange().authenticated()
    //         )
    //         .httpBasic(basic -> {})
    //         // 【重要】在安全链中禁用CORS，因为我们将使用独立的CorsWebFilter Bean来处理
    //         .cors(cors -> cors.disable())
    //         .csrf(csrf -> csrf.disable());
            
    //     return http.build();
    // }

    //  /**
    //  * 【新增】CORS配置源的Bean
    //  */
    // @Bean
    // public CorsConfigurationSource corsConfigurationSource() {
    //     CorsConfiguration configuration = new CorsConfiguration();
    //     // 允许来自我们Vue应用源的请求 (重要！)
    //     configuration.setAllowedOrigins(Arrays.asList("http://localhost:10481")); 
    //     // 允许所有请求方法 (GET, POST, PUT, DELETE, etc.)
    //     configuration.setAllowedMethods(Arrays.asList("*")); 
    //     // 允许所有请求头
    //     configuration.setAllowedHeaders(Arrays.asList("*"));
    //     // 允许浏览器发送Cookie等凭证
    //     configuration.setAllowCredentials(true); 

    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", configuration); // 对所有URL路径应用这个CORS配置
    //     return source;
    // }

    //     /**
    //  * 【新增】为WebFlux环境配置专用的CORS过滤器Bean
    //  */
    // @Bean
    // public CorsWebFilter corsWebFilter() {
    //     CorsConfiguration config = new CorsConfiguration();
    //     // 允许来自我们Vue应用源的请求 (重要！)
    //     config.setAllowedOrigins(Arrays.asList("http://localhost:10481")); // 请确保端口正确
    //     config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    //     config.setAllowedHeaders(Arrays.asList("*"));
    //     config.setAllowCredentials(true);

    //     // 使用WebFlux专用的 UrlBasedCorsConfigurationSource
    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", config); // 对所有路径生效

    //     return new CorsWebFilter(source);
    // }

      /**
     * 【修正】CORS配置源的Bean，现在直接在安全链中使用
     */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> originsList = Arrays.asList(allowedOrigins.split(","));
        config.setAllowedOrigins(originsList);
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    
}


