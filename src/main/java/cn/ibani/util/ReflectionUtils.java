package cn.ibani.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import cn.ibani.redis.annotation.RedisColumn;
import cn.ibani.redis.annotation.RedisId;
import cn.ibani.redis.dao.params.RedisColumnParams;
import cn.ibani.redis.dao.params.RedisEntityParams;
import cn.ibani.redis.dao.params.RedisKeyValue;
import cn.ibani.redis.exception.RedisException;

public class ReflectionUtils {
    
    
    public static List<String> getRedisColumn(RedisEntityParams entityParams){
        List<String> array = new ArrayList<String>();
        for(RedisColumnParams columnParams :entityParams.columns){
            array.add(columnParams.name);
        }
        return array;
    }

    
    public static  Map<String,String> getRedisColumnMap(Object obj,RedisEntityParams entityParams) throws RedisException{
        Map<String,String> map = new HashMap<String,String>();
        for(RedisColumnParams columnParams :entityParams.columns){
            Object value = null;
            try {
                value = columnParams.getter.invoke(obj, new Object[]{});
            }catch (Exception e) {
                e.printStackTrace();
                throw new RedisException(RedisException.ANNOTAION, obj.getClass().getName()+"getter调用失败:"+columnParams.name);
            }
            if(value!=null){
                map.put(columnParams.name,columnParams.parser.obj2Str(value,columnParams.format));
            }
        }
        return map;
    }


    
    
    public static String parseRedisColumnName(Method method) {
        String name = parseRedisFieldName(method.getName());
        RedisColumn column = method.getDeclaredAnnotation(RedisColumn.class);
        if(column!=null&&!StringUtils.isEmpty(column.name()))
            name=column.name();
        return name;
    }
    
    public static String parseRedisFieldName(String method) {
        String field =method.substring(4);
        field=(""+method.charAt(3)).toLowerCase()+field;
        return field;
    }
    public static String parseRedisColumnName(String column,String method){
        if(StringUtils.isEmpty(column)){
            return column;
        }
        return parseRedisFieldName(method);
    }
    public static String parseRedisGetMethodName(String field) {
        String method =(""+field.charAt(0)).toUpperCase()+field.substring(1);
        method="get"+method;
        return method;
    }
    public static String parseRedisSetMethodName(String field) {
        String method =field.substring(0)+(""+field.charAt(0)).toUpperCase();
        method="set"+method;
        return method;
    }
    public static String parseUniqueHashKey(String column,String uniqueHashKey){
        if(StringUtils.isEmpty(uniqueHashKey)){
            uniqueHashKey = column+".to.id";
        }
        return uniqueHashKey;
    }

    
    
    
    public static void setRedisId(Object obj,RedisColumnParams idColumn,Long value) throws RedisException{
        try {
            Method idSetMthod = idColumn.setter;
            idSetMthod.invoke(obj, value);
        }  catch (Exception e) {
            e.printStackTrace();
            throw new RedisException(RedisException.METHOD, obj.getClass().getName()+"setter调用失败:"+idColumn.name);
        }
    }
    
    
    public static Long getRedisId(Object obj) throws RedisException{
        Long id  = 0l;
        Method[] methods = obj.getClass().getMethods();
        Method idGetMethod = null;
        for(Method method:methods){
            if(method.getDeclaredAnnotation(RedisId.class)!=null){
                idGetMethod = method;
                break;
            }
        }
        
        if(idGetMethod==null){
            throw new RedisException(100,obj.getClass().getName()+"没有主键字段");
        }
        
        try {
            id = (Long)idGetMethod.invoke(obj, new Object[]{});
        }  catch (Exception e) {
            e.printStackTrace();
            throw new RedisException(100,obj.getClass().getName()+"主键字段设置数值失败");
        }
        return id;
    }
    
    
    
    public static List<Method> getRedisUniqueMethod(Class<?> obj){
        Method[] methods = obj.getMethods();
        List<Method> array = new ArrayList<Method>();
        for(Method method:methods){
            RedisColumn column = method.getDeclaredAnnotation(RedisColumn.class);
            if(column!=null&&column.unique()){
                array.add(method);
            }
        }
        return array;
    }
    
    public static Map<String,String> getRedisUniqueMethodMap(Object obj,RedisEntityParams entityParams) throws RedisException{
        Map<String,String> map = new HashMap<String, String>();
        for(RedisColumnParams columnParams :entityParams.columns){
            if(columnParams.unique){
                Object value = null;
                try {
                    value = columnParams.getter.invoke(obj);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RedisException(RedisException.METHOD, obj.getClass().getName()+"getter调用失败:"+columnParams.name);
                }
                if(value==null){
                    throw new RedisException(RedisException.METHOD, obj.getClass().getName()+"唯一键属性无值:"+columnParams.name);
                }
                map.put(columnParams.uniqueHashKey, columnParams.parser.obj2Str(value,columnParams.format));
            }
        }
        return map;
    }
    

    
    
    
    public static Map<String,RedisKeyValue> getRedisUniqueMethodToIdMap(Object obj,RedisEntityParams entityParams,Long id) throws RedisException{
        Map<String,RedisKeyValue> map = new HashMap<String,RedisKeyValue>();
        for(RedisColumnParams columnParams :entityParams.columns){
            if(columnParams.unique){
                try {
                    map.put(columnParams.uniqueHashKey,
                            new RedisKeyValue(
                                    columnParams.parser.obj2Str(columnParams.getter.invoke(obj),columnParams.format),
                                    id==null?null:id.toString()
                                    )
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RedisException(RedisException.METHOD, obj.getClass().getName()+"getter调用失败:"+columnParams.name);
                }
            }
        }
        return map;
    }
    
    public static void setRedisFields(Object obj,RedisEntityParams entityParams,Map<String,String> columns) throws RedisException{
        for(RedisColumnParams columnParams :entityParams.columns){
            String value = columns.get(columnParams.name);
            if(value!=null){
                try {
                    columnParams.setter.invoke(obj, columnParams.parser.str2Obj(columnParams.getter.getReturnType(),value,columnParams.format));
                }catch (Exception e) {
                    e.printStackTrace();
                    throw new RedisException(RedisException.METHOD, obj.getClass().getName()+"setter调用失败:"+columnParams.name);
                }
            }
        }
    }
}
