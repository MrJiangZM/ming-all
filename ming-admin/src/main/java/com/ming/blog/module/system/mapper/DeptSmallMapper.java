package com.ming.blog.module.system.mapper;

import com.ming.blog.base.BaseMapper;
import com.ming.blog.module.system.entity.Dept;
import com.ming.blog.module.system.pojo.dto.DeptSmallDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
* @author MrJiangZM
* @date 2019-03-25
*/
@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeptSmallMapper extends BaseMapper<DeptSmallDto, Dept> {

}