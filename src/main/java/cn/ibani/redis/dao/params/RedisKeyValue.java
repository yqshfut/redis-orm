package cn.ibani.redis.dao.params;

public class RedisKeyValue{
    public String key;
    public String value;
    public RedisKeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
