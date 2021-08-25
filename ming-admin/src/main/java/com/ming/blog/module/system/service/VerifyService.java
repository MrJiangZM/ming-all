package com.ming.blog.module.system.service;

import com.ming.blog.pojo.vo.EmailVO;

/**
 * @author
 * @date 2018-12-26
 */
public interface VerifyService {

    /**
     * 发送验证码
     *
     * @param email /
     * @param key   /
     *
     * @return /
     */
    EmailVO sendEmail(String email, String key);


    /**
     * 验证
     *
     * @param code /
     * @param key  /
     */
    void validated(String key, String code);
}
