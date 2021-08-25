package com.ming.blog.module.system.pojo.dto;

import com.ming.blog.anno.Query;
import lombok.Data;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author 公共查询类
 */
@Data
public class MenuQueryCriteria {

    @Query(blurry = "title,component,permission")
    private String blurry;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;

    @Query(type = Query.Type.IS_NULL, propName = "pid")
    private Boolean pidIsNull;

    @Query
    private Long pid;
}
