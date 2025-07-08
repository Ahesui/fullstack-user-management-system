package com.example.user_service.common;

import lombok.Data;

@Data
public class ApiResponse<T> {

    private Integer code;
    private String message;
    private T data;

    // 私有化构造函数，强制使用静态工厂方法创建实例
    private ApiResponse() {}

    // 成功，不返回数据
    public static <T> ApiResponse<T> success() {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("操作成功");
        return response;
    }

    // 成功，并返回数据
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("操作成功");
        response.setData(data);
        return response;
    }

    // 失败，返回自定义消息
    public static <T> ApiResponse<T> error(Integer code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}