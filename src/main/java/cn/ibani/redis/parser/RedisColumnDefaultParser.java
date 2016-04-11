package cn.ibani.redis.parser;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.StringUtils;

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
                format = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            try {
                obj = sdf.parse(value);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RedisException(RedisException.PARSER,
                        "日期类型解析为Date失败：" + value + ";" + format);
            }
        } else if (clz == String.class) {
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
                format = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            try {
                obj = sdf.format(obj);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RedisException(RedisException.PARSER,
                        "日期类型格式化为String失败：" + obj + ";" + format);
            }
        }
        return obj.toString();
    }

}
