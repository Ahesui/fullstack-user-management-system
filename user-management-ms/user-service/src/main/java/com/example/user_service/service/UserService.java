package com.example.user_service.service;

import com.example.user_service.entity.User;
import java.util.List;
import java.util.Optional; // 引入Optional
import com.github.pagehelper.PageInfo; // 引入PageInfo


/**
 * 用户服务层接口
 */
public interface UserService {

    /**
     * 分页获取所有用户
     * @param pageNum  当前页码
     * @param pageSize 每页显示条数
     * @param keyword 关键词
     * @return 包含了分页信息和用户列表的PageInfo对象
     */
    PageInfo<User> getAllUsers(int pageNum, int pageSize, String keyword);

    // /**
    //  * 获取所有用户
    //  * @return 用户列表
    //  */
    // List<User> getAllUsers();

    /**
     * 保存一个用户（新增或更新）
     * @param user 要保存的用户对象
     */
    void saveUser(User user);


    

// ... 已有方法 ...
Optional<User> findUserById(Long id);

// ... 已有方法 ...

/**
 * 根据ID删除用户
 * @param id 用户ID
 */
void deleteUserById(Long id);

}