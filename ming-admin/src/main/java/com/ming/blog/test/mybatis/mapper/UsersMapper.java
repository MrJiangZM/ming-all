package com.ming.blog.test.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ming.blog.test.mybatis.entity.Users;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author mybatis-generator
 */
public interface UsersMapper extends BaseMapper<Users> {

    @Select("select * from users where username = #{username}")
    Users findByUsername(@Param("username") String username);

}
