package com.ming.blog.module.system.repository;

import com.ming.blog.module.system.entity.DictDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

/**
 * @author
 * @date 2019-04-10
 */
public interface DictDetailRepository extends JpaRepository<DictDetail, Long>, JpaSpecificationExecutor<DictDetail> {

    /**
     * 根据字典名称查询
     *
     * @param name /
     *
     * @return /
     */
    List<DictDetail> findByDictName(String name);
}