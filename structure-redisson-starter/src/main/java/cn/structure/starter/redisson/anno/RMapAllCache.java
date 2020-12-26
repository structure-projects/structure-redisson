package cn.structure.starter.redisson.anno;

import java.lang.annotation.*;

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
public @interface RMapAllCache {
    /**
     * mapKey
     */
    String mapKey() default "";

    /**
     * keys
     */
    String keys() default "";

    /**
     * key name 用于补偿
     */
    String keyName()default "";

    /**
     * 时间设置
     */
    CTime time() default @CTime();
}
