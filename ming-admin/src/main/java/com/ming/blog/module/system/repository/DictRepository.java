package com.ming.blog.module.system.repository;

import com.ming.blog.module.system.entity.Dict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Set;

/**
 * @author
 * @date 2019-04-10
 */
public interface DictRepository extends JpaRepository<Dict, Long>, JpaSpecificationExecutor<Dict> {

    /**
     * 删除
     *
     * @param ids /
     */
    void deleteByIdIn(Set<Long> ids);

    /**
     * 查询
     *
     * @param ids /
     *
     * @return /
     */
    List<Dict> findByIdIn(Set<Long> ids);
}