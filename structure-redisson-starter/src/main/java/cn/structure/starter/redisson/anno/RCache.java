package cn.structure.starter.redisson.anno;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *     读缓存注解 默认读对象缓存支持写操作过程
 * </p>
 * @author chuck
 * @since 2020-12-23
 * @version 1.0.1
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RCache {
    /**
     * key
     * @return
     */
    String key() default "";

    /**
     * 设置默认时长
     */
    long time() default 1;

    /**
     * 设置单位
     */
    TimeUnit timeType() default TimeUnit.DAYS;

}
