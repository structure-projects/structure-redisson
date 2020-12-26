package cn.structure.starter.redission.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>
 * Redisson 启动器
 * </p>
 *
 * @author chuck
 * @version V1.0.0
 * @since 2020/12/26 11:17
 */
@SpringBootApplication
public class RedissonApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedissonApplication.class,args);
    }
}
