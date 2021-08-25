package com.ming.blog.test.cache;

import com.google.common.collect.Maps;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.Map;

/**
 * @author MrJiangZM
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private TestValueCache testValueCache;
    @Resource
    private TestHashCache testHashCache;

    @GetMapping("/test1")
    public Object test1() {
        testValueCache.set("test");
        testValueCache.set("test", "testtest");
        return true;
    }

    @GetMapping("/test2")
    public Object test2() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("test", testValueCache.get());
        map.put("testtest", testValueCache.get("test"));
        return map;
    }

    @GetMapping("/test3")
    public Object test3() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("test", testValueCache.get());
        map.put("testtest", testValueCache.get("test"));
        testHashCache.put("test", "test1", map);
        return true;
    }

    @GetMapping("/test4")
    public Object test4() {
        return testHashCache.get("test", "test1");
    }


}
