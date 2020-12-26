package cn.structure.starter.redisson.anno;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *     缓存时间策略配置
 * </p>
 * @author chuck
 * @since 2020-12-23
 * @version 1.0.1
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CTime{
    /**
     * 是否有时效果
     */
    boolean isTime() default false;

    /**
     * 时间限制
     */
    long time() default 0L;

    /**
     * 时间类型
     */
    TimeUnit timeType() default TimeUnit.MINUTES;
}