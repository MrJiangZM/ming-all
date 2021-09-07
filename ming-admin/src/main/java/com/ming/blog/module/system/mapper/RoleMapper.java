package com.ming.blog.module.system.mapper;

import com.ming.blog.base.BaseMapper;
import com.ming.blog.module.system.entity.Role;
import com.ming.blog.module.system.pojo.dto.RoleDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @author MrJiangZM
 * @date 2018-11-23
 */
@Mapper(componentModel = "spring", uses = {MenuMapper.class, DeptMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper extends BaseMapper<RoleDto, Role> {

}
