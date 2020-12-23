package cn.structure.starter.redisson.anno;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *     读取map集合中的缓存
 * </p>
 * @author chuck
 * @since 2020-12-23
 * @version 1.0.1
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RCacheMap {
    //mapKey
    String mapKey() default "";
    //list 关联情况下的KEY
    CList list() default @CList;
    //key
    String key() default "";
    //时间配置
    //是否有时效果
    boolean isTime() default false;
    //时间限制
    long time() default 0L;
    //时间类型
    TimeUnit timeType() default TimeUnit.MINUTES;
}
