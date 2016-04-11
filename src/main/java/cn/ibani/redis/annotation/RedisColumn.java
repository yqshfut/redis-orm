package cn.ibani.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.ibani.redis.parser.RedisColumnDefaultParser;
import cn.ibani.redis.parser.RedisColumnParser;
/**
 * 字段注解
 * @author sai
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisColumn {
    /**
     * 属性名称
     * @return
     */
    String name() default "";
    /**
     * 是否唯一
     * @return
     */
    boolean unique() default false;
    /**
     * 字段唯一时，字段与id映射的hash类型key
     * @return
     */
    String uniqueHashKey() default "";
    /**
     * 字段解析器
     * @return
     */
    Class<? extends RedisColumnParser> parser() default RedisColumnDefaultParser.class;
    /**
     * 字段解析器附带参数：字段格式化参数
     * @return
     */
    public String format() default "";
}
