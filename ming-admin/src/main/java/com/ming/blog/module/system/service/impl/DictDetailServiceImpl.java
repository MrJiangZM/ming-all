package com.ming.blog.module.system.service.impl;

import com.ming.blog.anno.CacheKey;
import com.ming.blog.config.PageUtil;
import com.ming.blog.config.QueryHelp;
import com.ming.blog.config.RedisUtils;
import com.ming.blog.config.ValidationUtil;
import com.ming.blog.module.system.entity.Dict;
import com.ming.blog.module.system.entity.DictDetail;
import com.ming.blog.module.system.pojo.dto.DictDetailDto;
import com.ming.blog.module.system.pojo.dto.DictDetailQueryCriteria;
import com.ming.blog.module.system.repository.DictDetailRepository;
import com.ming.blog.module.system.repository.DictRepository;
import com.ming.blog.module.system.service.DictDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author
 * @date 2019-04-10
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "dict")
public class DictDetailServiceImpl implements DictDetailService {

    private final DictRepository dictRepository;
    private final DictDetailRepository dictDetailRepository;
    private final RedisUtils redisUtils;

    @Override
    public Map<String, Object> queryAll(DictDetailQueryCriteria criteria, Pageable pageable) {
        Page<DictDetail> page = dictDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(dictDetail -> {
            DictDetailDto dto = new DictDetailDto();
            BeanUtils.copyProperties(dictDetail, dto);
            return dto;
        }));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DictDetail resources) {
        dictDetailRepository.save(resources);
        // 清理缓存
        delCaches(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DictDetail resources) {
        DictDetail dictDetail = dictDetailRepository.findById(resources.getId()).orElseGet(DictDetail :: new);
        ValidationUtil.isNull(dictDetail.getId(), "DictDetail", "id", resources.getId());
        resources.setId(dictDetail.getId());
        dictDetailRepository.save(resources);
        // 清理缓存
        delCaches(resources);
    }

    @Override
    @Cacheable(key = "'name:' + #p0")
    public List<DictDetailDto> getDictByName(String name) {
        List<DictDetail> byDictName = dictDetailRepository.findByDictName(name);
        return byDictName.stream().map(dictDetail -> {
            DictDetailDto dto = new DictDetailDto();
            BeanUtils.copyProperties(byDictName, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        DictDetail dictDetail = dictDetailRepository.findById(id).orElseGet(DictDetail :: new);
        // 清理缓存
        delCaches(dictDetail);
        dictDetailRepository.deleteById(id);
    }

    public void delCaches(DictDetail dictDetail) {
        Dict dict = dictRepository.findById(dictDetail.getDict().getId()).orElseGet(Dict :: new);
        redisUtils.del(CacheKey.DICT_NAME + dict.getName());
    }
}