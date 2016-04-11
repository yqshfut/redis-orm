package cn.ibani.redis.exception;

public class RedisException extends Exception{
    
    public final static int CONNECTION = 0;
    
    public final static int METHOD = 1;
    public final static int ANNOTAION = 2;
    public final static int PARSER = 3;
    
    
    
    private static final long serialVersionUID = 1L;
    private int code;
    private String msg;

    public RedisException(int code,String msg){
        super("´úÂë:"+code+";ÐÅÏ¢:"+msg);
        this.code = code;
        this.msg = msg;
    }

    
    
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    
}
