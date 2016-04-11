package cn.ibani.redis.dao;

import cn.ibani.redis.exception.RedisException;


public interface RedisDao<T> {
    
    public boolean save(T t) throws RedisException;
    
    public boolean delete(T t) throws RedisException;

    public boolean update(T oldT,T newT) throws RedisException;
    
    public T oneQuery(String field,String value)throws RedisException;
    
    public T oneQuery(Long id)throws RedisException;
}
