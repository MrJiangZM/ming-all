package com.ming.blog.module.system.mapper;

import com.ming.blog.base.BaseMapper;
import com.ming.blog.module.system.entity.Role;
import com.ming.blog.module.system.pojo.dto.RoleSmallDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @author MrJiangZM
 * @date 2019-5-23
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleSmallMapper extends BaseMapper<RoleSmallDto, Role> {

}
