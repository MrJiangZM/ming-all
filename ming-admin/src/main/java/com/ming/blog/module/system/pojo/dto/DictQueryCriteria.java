package com.ming.blog.module.system.pojo.dto;

import com.ming.blog.anno.Query;
import lombok.Data;

/**
 * @author 公共查询类
 */
@Data
public class DictQueryCriteria {

    @Query(blurry = "name,description")
    private String blurry;
}
