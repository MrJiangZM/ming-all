package com.ming.blog.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author MrJiangZM
 * @since <pre>2021/8/6</pre>
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public Object test() {
        return "test";
    }

}
