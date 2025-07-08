package com.example.user_service.entity;

import lombok.Data;
import java.time.LocalDateTime;

// 引入校验相关的注解
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

@Data
public class User {

    private Long id;

    @NotEmpty(message = "用户名不能为空") // 规则：不能为空字符串
    @Size(min = 2, max = 20, message = "用户名长度必须在2到20个字符之间") // 规则：长度限制
    private String username;

    @NotEmpty(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确") // 规则：必须是合法的Email格式
    private String email;

    @NotNull(message = "年龄不能为空") // 规则：不能为null
    @Min(value = 1, message = "年龄必须大于0") // 规则：最小值
    @Max(value = 150, message = "年龄不能大于150岁") // 规则：最大值
    private Integer age;

    private LocalDateTime createTime;
}