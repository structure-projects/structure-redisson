package cn.structure.starter.redisson.anno;

import java.lang.annotation.*;

/**
 * <p>
 *     写缓存注解
 * </p>
 * @author chuck
 * @since 2020-12-23
 * @version 1.0.1
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WCache {
    //keyName
    String key() default "";
    //是否對象緩存
    boolean isObjCache() default true;
    //更新集合配置
    CList list() default @CList;
    //Map 配置
    CMap map() default @CMap;
    //时间配置
    CTime time() default @CTime;
}
