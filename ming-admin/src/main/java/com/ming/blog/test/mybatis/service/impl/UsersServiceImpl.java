package com.ming.blog.test.mybatis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ming.blog.test.mybatis.entity.Users;
import com.ming.blog.test.mybatis.mapper.UsersMapper;
import com.ming.blog.test.mybatis.service.IUsersService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mybatis-generator
 * @since 2021-08-11
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {


    @Resource
    private UsersMapper usersMapper;

    @Override
    public Users findByUsername(String username) {
        return usersMapper.findByUsername(username);
    }

//
//    @Override
//    public Users saveEntity(Users users) {
//        return usersMapper.saveEntity(users);
//    }
}
