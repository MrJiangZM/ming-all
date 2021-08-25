package com.ming.blog.module.system.pojo.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author
 * @date 2019-03-25
 */
@Data
public class DeptQueryCriteria {

    private String name;

    private Boolean enabled;

    private Long pid;

    private Boolean pidIsNull;

    private List<Timestamp> createTime;
}