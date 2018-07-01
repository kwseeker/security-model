package top.kwseeker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.kwseeker.service.AsyncService;

@RestController
@RequestMapping("/async")
@EnableAsync
public class AnnotationAsyncController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AsyncService asyncService;

    @GetMapping("/annotation/test1")
    public String test1() {
        asyncService.asyncTest();   //耗时大约20秒
        logger.info("AnnotationAsyncController.test1(): "+ Thread.currentThread().getName());
        return "异步执行请求ing";
    }

}
