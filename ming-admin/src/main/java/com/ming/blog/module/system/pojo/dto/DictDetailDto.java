package com.ming.blog.module.system.pojo.dto;

import com.ming.blog.base.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

/**
 * @author
 * @date 2019-04-10
 */
@Getter
@Setter
public class DictDetailDto extends BaseDTO implements Serializable {

    private Long id;

    private DictSmallDto dict;

    private String label;

    private String value;

    private Integer dictSort;
}