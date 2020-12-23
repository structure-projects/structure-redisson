package cn.structure.starter.redisson.anno;

import cn.structure.starter.redisson.enumerate.LockModelEnum;

import java.lang.annotation.*;

/**
 * <p>
 *     锁注解
 * </p>
 * @author chuck
 * @since 2020-12-23
 * @version 1.0.1
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lock {
    /**
     * 锁的模式:如果不设置,自动模式,当参数只有一个.使用 REENTRANT 参数多个 MULTIPLE
     */
    LockModelEnum lockModel() default LockModelEnum.AUTO;

    /**
     *  如果keys有多个,如果不设置,则使用 联锁
     */
    String[] keys() default {};

    /**
     *  锁超时时间,默认30000毫秒(可在配置文件全局设置)
     */
    long lockWatchdogTimeout() default 30000;

    /**
     *  等待加锁超时时间,默认10000毫秒 -1 则表示一直等待(可在配置文件全局设置)
     */
    long attemptTimeout() default 10000;
}
