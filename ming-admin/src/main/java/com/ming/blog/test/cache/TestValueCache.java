package com.ming.blog.test.cache;

import com.ming.blog.cache.BaseValueCache;
import org.springframework.stereotype.Component;

/**
 * 验证redis相关代码
 */
@Component
public class TestValueCache extends BaseValueCache<String, String> {

    private String prefix = "test_value_";

    @Override
    protected String getPrefix() {
        return prefix;
    }
}
