package com.ming.blog.module.system.pojo.dto;

import com.ming.blog.anno.Query;
import lombok.Data;

/**
 * @author
 * @date 2019-04-10
 */
@Data
public class DictDetailQueryCriteria {

    @Query(type = Query.Type.INNER_LIKE)
    private String label;

    @Query(propName = "name", joinName = "dict")
    private String dictName;
}