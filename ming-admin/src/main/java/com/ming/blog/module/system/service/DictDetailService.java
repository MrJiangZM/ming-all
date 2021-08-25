package com.ming.blog.module.system.service;

import com.ming.blog.module.system.entity.DictDetail;
import com.ming.blog.module.system.pojo.dto.DictDetailDto;
import com.ming.blog.module.system.pojo.dto.DictDetailQueryCriteria;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

/**
 * @author
 * @date 2019-04-10
 */
public interface DictDetailService {

    /**
     * 创建
     *
     * @param resources /
     */
    void create(DictDetail resources);

    /**
     * 编辑
     *
     * @param resources /
     */
    void update(DictDetail resources);

    /**
     * 删除
     *
     * @param id /
     */
    void delete(Long id);

    /**
     * 分页查询
     *
     * @param criteria 条件
     * @param pageable 分页参数
     *
     * @return /
     */
    Map<String, Object> queryAll(DictDetailQueryCriteria criteria, Pageable pageable);

    /**
     * 根据字典名称获取字典详情
     *
     * @param name 字典名称
     *
     * @return /
     */
    List<DictDetailDto> getDictByName(String name);
}