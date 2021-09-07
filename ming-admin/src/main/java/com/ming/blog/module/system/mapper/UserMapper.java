package com.ming.blog.module.system.mapper;

import com.ming.blog.base.BaseMapper;
import com.ming.blog.module.system.entity.User;
import com.ming.blog.module.system.pojo.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @author MrJiangZM
 * @date 2018-11-23
 */
@Mapper(componentModel = "spring", uses = {RoleMapper.class, DeptMapper.class, JobMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends BaseMapper<UserDto, User> {
}
