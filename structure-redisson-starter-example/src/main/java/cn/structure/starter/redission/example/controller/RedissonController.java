package cn.structure.starter.redission.example.controller;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 *  redisson使用的控制器
 * </p>
 *
 * @author chuck
 * @version V1.0.0
 * @since 2020/12/26 11:26
 */
@RestController
public class RedissonController {

    @Resource
    private RedissonClient redissonClient;

    @RequestMapping("/setKey")
    public void setKey() {
        RBucket<String> test = redissonClient.getBucket("test");
        test.set("test redissonClient");
        System.out.println("test = " + test.get());
    }

    @RequestMapping("/getKey")
    public void getKey() {
        RBucket<String> test = redissonClient.getBucket("test");
        System.out.println("test = " + test.get());
    }
}
