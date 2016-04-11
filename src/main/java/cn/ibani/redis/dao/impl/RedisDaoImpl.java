package cn.ibani.redis.dao.impl;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Transaction;
import cn.ibani.redis.annotation.RedisColumn;
import cn.ibani.redis.annotation.RedisEntity;
import cn.ibani.redis.annotation.RedisId;
import cn.ibani.redis.annotation.RedisTransient;
import cn.ibani.redis.dao.RedisDao;
import cn.ibani.redis.dao.params.RedisColumnParams;
import cn.ibani.redis.dao.params.RedisEntityParams;
import cn.ibani.redis.dao.params.RedisKeyValue;
import cn.ibani.redis.dao.query.PageQuery;
import cn.ibani.redis.dao.query.PageResult;
import cn.ibani.redis.exception.RedisException;
import cn.ibani.redis.parser.RedisColumnDefaultParser;
import cn.ibani.util.GenericsUtils;
import cn.ibani.util.ReflectionUtils;

public class RedisDaoImpl<T> implements RedisDao<T>{

    @Autowired
    private JedisSentinelPool jedisPool;
    @SuppressWarnings("unchecked")
    protected Class<T> entityClass = GenericsUtils.getSuperClassGenricType(this.getClass());
    
    protected RedisEntityParams params = new RedisEntityParams();
    
    public RedisDaoImpl(){
        try {
            initParams();
        } catch (RedisException e) {
            e.printStackTrace();
        }
    }
    
    public Jedis getJedis() throws RedisException{
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("redis.properties");
        Properties pro = new Properties();
        String passwd = "";
        try {
            pro.load(is);
            passwd = pro.getProperty("redis.passwd");
        }catch(Exception e){
            e.printStackTrace();
            throw new RedisException(RedisException.CONNECTION,"Redis密码读取失败");
        }
        Jedis jedis = jedisPool.getResource();
        jedis.auth(passwd);
        return jedis;
    }
    
    public void initParams() throws RedisException{
        
        //获取注解并设置相应值
        RedisEntity entity = (RedisEntity)entityClass.getDeclaredAnnotation(RedisEntity.class);
        if(entity==null){
            throw new RedisException(RedisException.ANNOTAION, entityClass.getName()+"未定义注解:"+RedisEntity.class.getName());
        }
        
        params.name = !StringUtils.isEmpty(entity.name())?entity.name():entityClass.getSimpleName();
        params.setKeyList = !StringUtils.isEmpty(entity.setKeyList())?entity.setKeyList():params.name+"s";
        params.keyCount = !StringUtils.isEmpty(entity.keyCount())?entity.keyCount():params.name+"_count";
        params.keyIndexCount = !StringUtils.isEmpty(entity.keyIndexCount())?entity.keyIndexCount():params.name+"_index";
        params.hashKeyAttr = !StringUtils.isEmpty(entity.hashKeyAttr())?entity.hashKeyAttr():params.name+":id";
        Method[] methods = entityClass.getMethods();
        for(Method method:methods){
            if(method.getName().startsWith("get")&&!method.getName().endsWith("getClass")){
                if(method.getDeclaredAnnotation(RedisTransient.class)!=null)
                    continue;
                Method getter = method;
                String field = ReflectionUtils.parseRedisFieldName(method.getName());
            
                
                RedisColumnParams columnParam = new RedisColumnParams();
                RedisColumn column  = getter.getAnnotation(RedisColumn.class);
                if(column!=null){
                    columnParam.name = column.name();
                    if(StringUtils.isEmpty(columnParam.name))
                        columnParam.name = field;
                    columnParam.unique = column.unique();
                    columnParam.uniqueHashKey = column.uniqueHashKey();
                    if(columnParam.unique&&StringUtils.isEmpty(columnParam.uniqueHashKey))
                        columnParam.uniqueHashKey = columnParam.name+".to.id";
                    columnParam.format = column.format();
                    try {
                        columnParam.parser = column.parser().newInstance();
                    }  catch (Exception e) {
                        e.printStackTrace();
                        throw new RedisException(RedisException.ANNOTAION, entityClass.getName()+"column重复:"+columnParam.name);
                    }
                }else{
                    columnParam.name = field;
                    columnParam.unique = false;
                    columnParam.parser = new RedisColumnDefaultParser();
                    columnParam.format = "";
                }
                
                if(params.columns.contains(columnParam)){
                    throw new RedisException(RedisException.ANNOTAION, entityClass.getName()+"column重复:"+columnParam.name);
                }
                
                
                try {
                    Method setter = entityClass.getMethod(getter.getName().replaceFirst("get", "set"), getter.getReturnType());
                    columnParam.setter = setter;
                    columnParam.getter = getter;
                }  catch (Exception e) {
                    e.printStackTrace();
                    throw new RedisException(RedisException.METHOD, entityClass.getName()+"setter错误:"+columnParam.name);
                }
                
                if(getter.getAnnotation(RedisId.class)!=null){
                    params.idColumn = columnParam;
                    if(getter.getReturnType()!=Long.class){
                        throw new RedisException(RedisException.METHOD, entityClass.getName()+"主键不为Long类型:"+columnParam.name);
                    }
                }else{
                    params.columns.add(columnParam);
                }
                
            }
        }
    }
    
    

    @Override
    public boolean save(T t) throws RedisException {
        
        Jedis jedis = getJedis();
        jedis.watch(params.keyIndexCount);
        
        //获取主键
        String idStr = jedis.get(params.keyIndexCount);
        Long id = idStr==null?1:Long.parseLong(idStr)+1;
        //设置主键
        ReflectionUtils.setRedisId(t, params.idColumn,id);
        
        
        Transaction transaction =  jedis.multi();
        String hashKeyAttr = params.hashKeyAttr.replace("id", ""+id);
        //添加到列表
        transaction.zadd(params.setKeyList, id,hashKeyAttr);
        //设置属性
        transaction.hmset(hashKeyAttr, ReflectionUtils.getRedisColumnMap(t,params));
        //增加数目
        transaction.incr(params.keyCount);
        //增加主键索引
        transaction.incr(params.keyIndexCount);
        
        //设置唯一键
        Map<String,RedisKeyValue> uniques = ReflectionUtils.getRedisUniqueMethodToIdMap(t,params,id);
        for(String hashKeyIndex:uniques.keySet()){
            RedisKeyValue kv = uniques.get(hashKeyIndex);
            transaction.hset(hashKeyIndex, kv.key, id.toString());
        }
        List<Object> results   = transaction.exec();
        if(results!=null){
            System.out.println(results2Str(results));
            return true;
        }
        jedis.close();
        return false;
    }
    
    
    public boolean delete(T t) throws RedisException{
        Jedis jedis = getJedis();
        jedis.watch(params.keyCount);
        Long id = ReflectionUtils.getRedisId(t);
        Transaction transaction =  jedis.multi();
        String hashKeyAttr = params.hashKeyAttr.replace("id", ""+id);
        
        //删除列表中一项
        transaction.zrem(params.setKeyList, hashKeyAttr);
        //删除属性
        for(RedisColumnParams columnParams:params.columns){
            transaction.hdel(hashKeyAttr, columnParams.name);
        }
        //删除数目
        transaction.decr(params.keyCount);
        
        //删除唯一键
        Map<String,String> uniques= ReflectionUtils.getRedisUniqueMethodMap(t,params);
        for(String key:uniques.keySet()){
            transaction.hdel(key, uniques.get(key));
        }
        List<Object> results   = transaction.exec();
        if(results!=null){
            System.out.println(results2Str(results));
            return true;
        }
        jedis.close();
        return false;
    }
    
    public boolean update(T oldT,T newT) throws RedisException{
        
        //获取主键
        Long id = ReflectionUtils.getRedisId(newT);
        String hashKeyAttr = params.hashKeyAttr.replace("id", ""+id);
        Jedis jedis = getJedis();
        jedis.watch(hashKeyAttr.getBytes());
        
        
        Transaction transaction =  jedis.multi();
        
        //设置属性
        for(RedisColumnParams columnParams:params.columns){
            transaction.hdel(hashKeyAttr,columnParams.name);
        }
        transaction.hmset(hashKeyAttr, ReflectionUtils.getRedisColumnMap(newT,params));
        
        //设置唯一键
        Map<String,String> oldUniques = ReflectionUtils.getRedisUniqueMethodMap(oldT, params);
        for(String hashKeyIndex:oldUniques.keySet()){
            transaction.hdel(hashKeyIndex, oldUniques.get(hashKeyIndex));
        }
        
        Map<String,String> newUniques = ReflectionUtils.getRedisUniqueMethodMap(newT, params);
        for(String hashKeyIndex:oldUniques.keySet()){
            transaction.hset(hashKeyIndex,newUniques.get(hashKeyIndex), id.toString());
        }
        
        List<Object> results   = transaction.exec();
        if(results!=null){
            System.out.println(results2Str(results));
            return true;
        }
        jedis.close();
        return false;
    }
    
    
    public PageResult<T> pageQuery(PageQuery query){
        PageResult<T> result = new PageResult<T>();
        
        return result;
    }
    
    public T oneQuery(String column,String value) throws RedisException{
        RedisColumnParams targetColumn = null;
        for(RedisColumnParams columnParams:params.columns){
            if(columnParams.name.equals(column)){
                if(!columnParams.unique)
                    throw new RedisException(RedisException.METHOD, entityClass.getName()+"column不是唯一字段:"+columnParams.name);
                targetColumn = columnParams;
                break;
            }
        }
        if(targetColumn==null){
            throw new RedisException(RedisException.METHOD, entityClass.getName()+"column字段不存在:"+column);
        }
        T t = null;
        Jedis jedis = getJedis();
        Long id = Long.parseLong(jedis.hget(targetColumn.uniqueHashKey,value));
        t = oneQuery(id);
        jedis.close();
        return t;
    }
    
    public T oneQuery(Long id) throws RedisException{
        T t = null;
        try {
            t = entityClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Jedis jedis = getJedis();
        ReflectionUtils.setRedisId(t, params.idColumn, id);
        Map<String,String> fields = jedis.hgetAll(params.hashKeyAttr.replace("id", id.toString()));
        ReflectionUtils.setRedisFields(t, params,fields);
        return t;
    }
    
    
    private String results2Str(List<Object> results) {
        StringBuffer sb = new StringBuffer();
        for (Object obj : results) {
            sb.append(obj).append("-");
        }
        return sb.toString();
    }

}

