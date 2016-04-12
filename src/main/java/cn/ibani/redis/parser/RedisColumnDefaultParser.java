package cn.ibani.redis.parser;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.TemporalType;

import org.springframework.util.StringUtils;

import cn.ibani.redis.annotation.RedisFormat;
import cn.ibani.redis.exception.RedisException;

public class RedisColumnDefaultParser implements RedisColumnParser {

    @Override
    public Object str2Obj(Class<?> clz, String value, String format)
            throws RedisException {
        Object obj = null;
        if (clz == Long.class) {
            obj = Long.parseLong(value);
        } else if (clz == Date.class) {
            if (StringUtils.isEmpty(format))
                format = RedisFormat.DATE_TIMESTAMP;
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            try {
                obj = sdf.parse(value);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RedisException(RedisException.PARSER,
                        "日期类型解析为Date失败：" + value + ";" + format);
            }
        } else if(clz.isEnum()){
            if (StringUtils.isEmpty(format))
                format = RedisFormat.ENUM_STRING;
            Object[] ems = clz.getEnumConstants();
            if (format.equals(RedisFormat.ENUM_ORDINAL)) {
                try {
                    obj = ems[Integer.parseInt(value)];
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RedisException(RedisException.PARSER, "枚举类型解析为"
                            + clz.getName() + "失败：" + value + ";" + format);
                }
            } else {
                for (Object em : ems) {
                    if (em.toString().equals(value)) {
                        obj = em;
                        break;
                    }
                }
            }
        }else if (clz == String.class) {
            obj = value;
        } else {
            throw new RedisException(RedisException.PARSER, "默认解析器不支持此类型"
                    + clz.getName());
        }
        return obj;
    }

    @Override
    public String obj2Str(Object obj, String format) throws RedisException {
        Class<?> clz = obj.getClass();
        if (clz == Date.class) {
            if (StringUtils.isEmpty(format))
                format = RedisFormat.DATE_TIMESTAMP;
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            try {
                return sdf.format(obj);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RedisException(RedisException.PARSER,
                        "日期类型格式化为String失败：" + obj + ";" + format);
            }
        }else if(obj.getClass().isEnum()){
            if (StringUtils.isEmpty(format))
                format = RedisFormat.ENUM_STRING;
             if(RedisFormat.ENUM_ORDINAL.equals(format)){
                 return ""+((Enum<?>)obj).ordinal();
             }
        }
        return obj.toString();
    }
    
    public static void main(String[] args) throws Exception {
       RedisColumnDefaultParser parser = new RedisColumnDefaultParser();
       
       System.out.println(parser.str2Obj(Date.class, "2016-4-12 16:40:00", RedisFormat.DATE_TIMESTAMP));
       System.out.println(parser.obj2Str(new Date(), RedisFormat.DATE_TIMESTAMP));
       
       System.out.println(parser.str2Obj(TemporalType.class, "DATE", RedisFormat.ENUM_STRING));
       System.out.println(parser.str2Obj(TemporalType.class, "0", RedisFormat.ENUM_ORDINAL));
       
       System.out.println(parser.obj2Str(TemporalType.DATE, RedisFormat.ENUM_STRING));
       System.out.println(parser.obj2Str(TemporalType.DATE, RedisFormat.ENUM_ORDINAL));
    }

}
