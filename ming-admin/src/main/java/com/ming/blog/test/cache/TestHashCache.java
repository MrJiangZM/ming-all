package com.ming.blog.test.cache;

import com.ming.blog.cache.BaseHashCache;
import com.ming.blog.cache.BaseValueCache;
import org.springframework.stereotype.Component;

/**
 * 验证redis相关代码
 */
@Component
public class TestHashCache extends BaseHashCache<String, String, Object> {

    private String prefix = "test_hash_";

    @Override
    protected String getPrefix() {
        return prefix;
    }
}
