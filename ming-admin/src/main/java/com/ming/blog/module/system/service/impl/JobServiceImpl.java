package com.ming.blog.module.system.service.impl;

import com.ming.blog.exceptions.BadRequestException;
import com.ming.blog.anno.CacheKey;
import com.ming.blog.exceptions.EntityExistException;
import com.ming.blog.util.FileUtil;
import com.ming.blog.config.PageUtil;
import com.ming.blog.config.QueryHelp;
import com.ming.blog.config.RedisUtils;
import com.ming.blog.config.ValidationUtil;
import com.ming.blog.module.system.entity.Job;
import com.ming.blog.module.system.pojo.dto.JobDto;
import com.ming.blog.module.system.pojo.dto.JobQueryCriteria;
import com.ming.blog.module.system.repository.JobRepository;
import com.ming.blog.module.system.repository.UserRepository;
import com.ming.blog.module.system.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author
 * @date 2019-03-29
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "job")
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final RedisUtils redisUtils;
    private final UserRepository userRepository;

    @Override
    public Map<String, Object> queryAll(JobQueryCriteria criteria, Pageable pageable) {
        Page<Job> page = jobRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
//        return PageUtil.toPage(page.map(jobMapper::toDto).getContent(),page.getTotalElements());
        return PageUtil.toPage(page.map(dictDetail -> {
            JobDto dto = new JobDto();
            BeanUtils.copyProperties(dictDetail, dto);
            return dto;
        }).getContent(), page.getTotalElements());
    }

    @Override
    public List<JobDto> queryAll(JobQueryCriteria criteria) {
        List<Job> list = jobRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
//        return jobMapper.toDto(list);
        return list.stream().map(dictDo -> {
            JobDto dto = new JobDto();
            BeanUtils.copyProperties(dictDo, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Cacheable(key = "'id:' + #p0")
    public JobDto findById(Long id) {
        Job job = jobRepository.findById(id).orElseGet(Job :: new);
        ValidationUtil.isNull(job.getId(), "Job", "id", id);
//        return jobMapper.toDto(job);
        JobDto dto = new JobDto();
        BeanUtils.copyProperties(job, dto);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Job resources) {
        Job job = jobRepository.findByName(resources.getName());
        if (job != null) {
            throw new EntityExistException(Job.class, "name", resources.getName());
        }
        jobRepository.save(resources);
    }

    @Override
    @CacheEvict(key = "'id:' + #p0.id")
    @Transactional(rollbackFor = Exception.class)
    public void update(Job resources) {
        Job job = jobRepository.findById(resources.getId()).orElseGet(Job :: new);
        Job old = jobRepository.findByName(resources.getName());
        if (old != null && !old.getId().equals(resources.getId())) {
            throw new EntityExistException(Job.class, "name", resources.getName());
        }
        ValidationUtil.isNull(job.getId(), "Job", "id", resources.getId());
        resources.setId(job.getId());
        jobRepository.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        jobRepository.deleteAllByIdIn(ids);
        // 删除缓存
        redisUtils.delByKeys(CacheKey.JOB_ID, ids);
    }

    @Override
    public void download(List<JobDto> jobDtos, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (JobDto jobDTO : jobDtos) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("岗位名称", jobDTO.getName());
            map.put("岗位状态", jobDTO.getEnabled() ? "启用" : "停用");
            map.put("创建日期", jobDTO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void verification(Set<Long> ids) {
        if (userRepository.countByJobs(ids) > 0) {
            throw new BadRequestException("所选的岗位中存在用户关联，请解除关联再试！");
        }
    }
}