package cn.structure.starter.redission.example.controller;

import cn.structure.starter.redisson.anno.Lock;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * Redisson锁的控制器
 * </p>
 *
 * @author chuck
 * @version V1.0.0
 * @since 2020/12/26 11:27
 */
@RestController
public class RedissonLockController {


    @RequestMapping("/testLock")
    @Lock(keys = "#key")
    public void testLock(@RequestParam("key") String key) throws InterruptedException {
        System.out.println("key = " + key);
        Thread.sleep(10000L);
    }

    @RequestMapping("/testLock2")
    @Lock(keys = "#testVO.id")
    public void testLockObject(@RequestBody TestVO testVO ) throws InterruptedException {
        System.out.println("key = " + testVO.getId());
        Thread.sleep(10000L);
    }


}
