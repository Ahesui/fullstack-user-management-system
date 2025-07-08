package com.example.user_service.mapper;

import com.example.user_service.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options; // 需要引入这个
import java.util.Optional; // 引入Optional
import java.util.List;
import org.apache.ibatis.annotations.Param; // 引入Param

/**
 * 用户数据访问层接口
 */
@Mapper // 告诉SpringBoot这是一个Mybatis的Mapper接口，需要为它创建实现类并加入Spring容器
public interface UserMapper {

    // /**
    //  * 查询所有用户
    //  * @return 用户列表
    //  */
    // List<User> findAll();

    // // 在insert方法的参数前，加上@Param("user")
    // @Options(useGeneratedKeys = true, keyProperty = "user.id", keyColumn = "id")
    // int insert(@Param("user") User user);
    /**
     * 插入一个新用户
     * @param user 包含了新用户信息的对象
     * @return 返回影响的行数，通常是1
     */
    // 使用 @Options 注解来获取数据库自增生成的主键ID
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(User user);

/**
 * 根据ID查询用户
 * @param id 用户ID
 * @return 返回一个可能包含User的Optional对象
 */
Optional<User> findById(Long id);
    


/**
 * 更新一个已存在的用户
 * @param user 包含了更新后信息的对象
 * @return 返回影响的行数，通常是1
 */
int update(User user);

// ... 已有方法 ...

/**
 * 根据ID删除用户
 * @param id 要删除的用户ID
 * @return 返回影响的行数，通常是1
 */
int deleteById(Long id);

/**
 * 查询所有用户（支持按用户名模糊查询）
 * @param keyword 查询关键字
 * @return 用户列表
 */
List<User> findAll(@Param("keyword") String keyword);
}