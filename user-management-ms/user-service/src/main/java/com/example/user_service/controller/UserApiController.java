package com.example.user_service.controller;

import com.example.user_service.common.ApiResponse;
import com.example.user_service.config.UserContextFilter;
import com.example.user_service.entity.User;
import com.example.user_service.service.UserService;
import com.github.pagehelper.PageInfo;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;


/**
 * 提供用户相关的API接口
 */
@RestController
@RequestMapping("/api/users") // 为这个Controller下的所有接口添加一个统一的前缀 "/api/users"
// @PreAuthorize("hasRole('ADMIN')") // 在类级别上要求所有接口都需要ADMIN角色
public class UserApiController {

    @Autowired
    private UserService userService;

    /**
     * API接口：获取用户列表（分页、条件查询）
     * @param pageNum
     * @param pageSize
     * @param keyword
     * @return 统一的JSON响应结果
     */
    @GetMapping
    public ApiResponse<PageInfo<User>> listUsers(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "") String keyword) {
        
        PageInfo<User> pageInfo = userService.getAllUsers(pageNum, pageSize, keyword);
        
        // 使用我们封装的ApiResponse来包装返回数据
        return ApiResponse.success(pageInfo);
    }


     /**
     * API - 根据ID获取单个用户信息
     */
    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        return userService.findUserById(id)
                .map(ApiResponse::success) // 如果Optional有值，则调用Result.success(user)
                .orElse(ApiResponse.error(404, "User not found with id: " + id));
    }

    /**
     * API - 新增用户
     * @RequestBody 会将请求体中的JSON字符串自动反序列化为User对象
     */
    @PostMapping
    public ApiResponse<?> createUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 处理校验错误，可以更详细地返回错误信息
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ApiResponse.error(400, errorMessage);
        }
        // 强制将ID设为null，确保是新增操作
        user.setId(null); 
        userService.saveUser(user);
        return ApiResponse.success();
    }

    /**
     * API - 更新用户
     */
    @PutMapping("/{id}")
    public ApiResponse<?> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        // 检查URL中的ID和数据库中是否存在该用户
        if (userService.findUserById(id).isEmpty()) {
            return ApiResponse.error(404, "User not found with id: " + id);
        }
        user.setId(id); // 使用URL中的ID，防止请求体中的ID被篡改
        userService.saveUser(user);
        return ApiResponse.success();
    }

    // /**
    //  * API - 删除用户
    //  */
    // @DeleteMapping("/{id}")
    // public ApiResponse<?> deleteUser(@PathVariable Long id) {
    //     if (userService.findUserById(id).isEmpty()) {
    //         return ApiResponse.error(404, "User not found with id: " + id);
    //     }
    //     userService.deleteUserById(id);
    //     return ApiResponse.success();
    // }

    
@DeleteMapping("/{id}")
public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable Long id) {
    // 手动进行授权检查
    UserContextFilter.UserContext currentUser = UserContextFilter.getCurrentUser();
    if (currentUser == null || !currentUser.hasRole("ROLE_ADMIN")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(403, "Access Denied"));
    }

    if (userService.findUserById(id).isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(404, "User not found"));
    }
    userService.deleteUserById(id);
    // 可以在日志中记录是哪个用户执行了操作
    System.out.println("User " + id + " deleted by: " + currentUser.getUsername());
    return ResponseEntity.ok(ApiResponse.success());
}
}