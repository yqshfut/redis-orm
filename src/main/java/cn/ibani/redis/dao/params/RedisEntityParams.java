package cn.ibani.redis.dao.params;

import java.util.HashSet;
import java.util.Set;



public class RedisEntityParams {

    public String name;
    public String setKeyList;
    public String keyCount;
    public String keyIndexCount;
    public String hashKeyAttr;

    public RedisColumnParams idColumn;
    public Set<RedisColumnParams> columns = new HashSet<RedisColumnParams>();

//  public DualHashBidiMap columnField = new DualHashBidiMap();
//  public Map<String,String> attrs;
//  public Map<String,RedisKeyValue> uniques;
    
    
    
}
