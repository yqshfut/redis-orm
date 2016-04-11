package cn.ibani.redis.dao.params;

import java.lang.reflect.Method;

import cn.ibani.redis.parser.RedisColumnParser;

public class RedisColumnParams {

    public String name;
    public boolean unique;
    public String uniqueHashKey;
    public RedisColumnParser parser;
    /**用于parser格式化使用***/
    public String format;
    public Method getter;
    public Method setter;
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RedisColumnParams other = (RedisColumnParams) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
