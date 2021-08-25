package com.ming.blog.module.system.pojo.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * @author
 * @date 2018-11-23
 */
@Data
public class RoleSmallDto implements Serializable {

    private Long id;

    private String name;

    private Integer level;

    private String dataScope;
}
