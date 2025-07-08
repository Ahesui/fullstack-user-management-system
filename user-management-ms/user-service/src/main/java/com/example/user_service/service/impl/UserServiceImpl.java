package com.example.user_service.service.impl;

import com.example.user_service.entity.User;
import com.example.user_service.mapper.UserMapper;
import com.example.user_service.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional; // 引入Optional
import java.util.List;



/**
 * 用户服务层实现类
 */
@Service // 告诉Spring，这是一个Service组件，请把它加入到IoC容器中进行管理
public class UserServiceImpl implements UserService {

    // 从Spring容器中注入UserMapper，以便调用它的方法
    @Autowired
    private UserMapper userMapper;

    // @Override // 实现了UserService接口中的方法
    // public List<User> getAllUsers() {
    //     // 这里的业务逻辑很简单，就是直接调用mapper层的方法
    //     return userMapper.findAll();
    // }


    @Override
public Optional<User> findUserById(Long id) {
    return userMapper.findById(id);
}

@Override
@CacheEvict(value = "users", allEntries = true) // 清空"users"缓存下的所有条目
@Transactional // 确保方法在事务中
public void saveUser(User user) {
    if (user.getId() == null) {
        // ID为空，说明是新用户，执行插入操作
        // 插入前可以添加业务逻辑，比如检查邮箱是否已存在等
        userMapper.insert(user);
        // messageProducerService.sendUserCreatedMessage(user);
        System.out.println("--- [UserService] Publishing UserCreatedEvent for user ID: " + user.getId());
    } else {
        // ID不为空，说明是老用户，执行更新操作
        userMapper.update(user);
    }
}

// ... 已有方法 ...

@Override
@CacheEvict(value = "users", allEntries = true) // 清空"users"缓存下的所有条目
public void deleteUserById(Long id) {
    userMapper.deleteById(id);
}

@Override
@Cacheable(value = "users", key = "'page:' + #pageNum + ':size:' + #pageSize + ':keyword:' + #keyword")
public PageInfo<User> getAllUsers(int pageNum, int pageSize, String keyword) {
    // 1. 设置分页参数
    PageHelper.startPage(pageNum, pageSize);

    // 2. 执行查询 (必须紧跟在startPage后面)
    // PageHelper会拦截这个查询，并自动在SQL后加上limit语句
    List<User> userList = userMapper.findAll(keyword);

    // 3. 用PageInfo对结果进行包装
    return new PageInfo<>(userList);
}

}