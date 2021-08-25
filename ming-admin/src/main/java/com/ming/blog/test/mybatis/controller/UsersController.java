package com.ming.blog.test.mybatis.controller;


import com.ming.blog.test.mybatis.entity.Users;
import com.ming.blog.test.mybatis.service.IUsersService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author mybatis-generator
 * @since 2021-08-11
 */
@RestController
@RequestMapping("/users")
public class UsersController {

    @Resource
    private IUsersService usersService;

    @PostMapping("/test1")
    public Object save(@RequestBody Users users) {
        usersService.save(users);
        return usersService.findByUsername(users.getUsername());
    }

}

