package cn.structure.starter.redisson.anno;

import java.lang.annotation.*;

/**
 * <p>
 *     对redis-Map存储结构封装map缓存注解 可以搭配 {@link CList} List 结构和对象结构混合使用
 * </p>
 * @author chuck
 * @since 2020-12-23
 * @version 1.0.1
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CMap {
    /**
     * 更新Map的key
     */
    String mapKey() default "";

    /**
     * 是否存入map集合中
     */
    boolean isMap() default false;

    /**
     * Map的时效设置 {@link CTime}
     */
    CTime time() default @CTime();
}