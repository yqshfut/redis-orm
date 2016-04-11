package cn.ibani.redis.parser;

import cn.ibani.redis.exception.RedisException;

public interface RedisColumnParser {
    public Object str2Obj(Class<?> clz,String str,String format)  throws RedisException;
    public String obj2Str(Object obj,String format) throws RedisException;
}
