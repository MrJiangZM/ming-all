package com.ming.blog.module.system.pojo.dto;

import com.ming.blog.anno.Query;
import lombok.Data;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author 公共查询类
 */
@Data
public class RoleQueryCriteria {

    @Query(blurry = "name,description")
    private String blurry;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
