package cn.structure.starter.redisson.anno;

import java.lang.annotation.*;


/**
 * <p>
 *   对redisList存储结构封装list缓存注解
 *   <pre>
 *   可以搭配{@link CMap} redisson 的map结构和对象结构混合使用
 *   </pre>
 * @author chuck
 * @since 2020-12-23
 * @version 1.0.1
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CList {
    /**
     * 是否为list集合
     */
    boolean isList() default false;
    /**
     * 更新集合的key
     */
    String listKeyName() default "";

    /**
     * 集合的类型 {@link ListType}
     */
    ListType value() default ListType.DATA;

    /**
     * 和 map 关联的KEY当value为 ListType.MAP 时生效
     */
    String mapKey() default "";

    /**
     * 集合长度
     */
    int size() default 200;

    /**
     * 列表的时效设置,缓存策略{@link CTime}
     */
    CTime time() default @CTime();

    enum  ListType {
        /**
         *     KEY 是指存储的集合对象只存储了数据中的key部分
         *     不太建议只存储KEY -- 双向没有时效限制时可以使用key 或者说时效比较长在定时更新范围内
         */
        KEY(),
        /**
         * DATA 是存储的集合结构中的数据是个完整的对象
         */
        DATA(),
        /**
         * MAP 是存储的集合对象实际上是以map集合形式存储的
         */
        MAP(),
    }
}
