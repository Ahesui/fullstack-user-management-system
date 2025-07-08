package com.example.gateway_service.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

// import java.util.List;
// import java.util.stream.Collectors;
// import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.userdetails.MapReactiveUserDetailsService; // 导入
import org.springframework.security.crypto.password.PasswordEncoder; // 导入
import com.example.gateway_service.dto.LoginRequest; // 导入DTO

import com.example.gateway_service.utils.JwtUtil;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private MapReactiveUserDetailsService userDetailsService; // 注入
    @Autowired
    private PasswordEncoder passwordEncoder; // 注入

    /**
     * 【已修正】手动处理登录逻辑
     * @param loginRequest 包含用户名和密码的请求体
     * @return 包含JWT的响应或错误响应
     */
    @PostMapping("/login")
    public Mono<ResponseEntity<?>> login(@RequestBody LoginRequest loginRequest) {
        return userDetailsService.findByUsername(loginRequest.getUsername())
                .flatMap(userDetails -> {
                    // 检查密码是否匹配
                    if (passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
                        // 密码正确，生成JWT
                        // 我们需要一个临时的Authentication对象来生成token
                        Authentication tempAuth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        String token = jwtUtil.generateToken(tempAuth);
                        Map<String, String> response = new HashMap<>();
                        response.put("token", token);
                        return Mono.just(ResponseEntity.ok(response));
                    } else {
                        // 密码错误
                        return Mono.just(ResponseEntity.status(401).body("Invalid credentials"));
                    }
                })
                // 用户名不存在
                .defaultIfEmpty(ResponseEntity.status(401).body("Invalid credentials"));
    }


    // /**
    //  * 登录接口。这个接口本身受Spring Security保护。
    //  * 前端调用此接口时，必须在Header中提供Basic Auth凭证。
    //  * 如果凭证正确，请求会到达这里；如果错误，Spring Security会直接返回401。
    //  * @param authentication Spring Security自动注入的、已认证成功的用户对象
    //  * @return 包含用户名和角色的用户信息
    //  */
    // @PostMapping("/login")
    // public ResponseEntity<?> login(Authentication authentication) {
    //     String token = jwtUtil.generateToken(authentication);
    //     Map<String, String> response = new HashMap<>();
    //     response.put("token", token);
    //     return ResponseEntity.ok(response);
    // }



}