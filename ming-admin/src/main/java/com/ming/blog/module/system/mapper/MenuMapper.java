package com.ming.blog.module.system.mapper;

import com.ming.blog.base.BaseMapper;
import com.ming.blog.module.system.entity.Menu;
import com.ming.blog.module.system.pojo.dto.MenuDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @author MrJiangZM
 * @date 2018-12-17
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MenuMapper extends BaseMapper<MenuDto, Menu> {
}
