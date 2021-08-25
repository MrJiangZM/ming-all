package com.ming.blog.test.mybatis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ming.blog.test.mybatis.entity.Users;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author mybatis-generator
 * @since 2021-08-11
 */
public interface IUsersService extends IService<Users> {

//    Users saveEntity(Users users);

    Users findByUsername(String username);
}
