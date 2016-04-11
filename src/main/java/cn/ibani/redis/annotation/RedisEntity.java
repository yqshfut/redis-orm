package cn.ibani.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 实体注解
 * @author sai
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisEntity {
    /**
     * 名称前缀
     * @return
     */
    public String name() default "";
    /**
     * 列表
     * @return
     */
    public String setKeyList()default "";
    /**
     * 数量-数据库实体数量
     * @return
     */
    public String keyCount() default "";
    /**
     * 索引位置-计算主键
     * @return
     */
    public String keyIndexCount() default "";
    
    /**
     * 属性-实体的所有属性
     * @return
     */
    public String hashKeyAttr() default "";
}
