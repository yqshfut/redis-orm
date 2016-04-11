package cn.ibani.user.bean;

import java.io.Serializable;
import java.util.Date;

import cn.ibani.redis.annotation.RedisColumn;
import cn.ibani.redis.annotation.RedisEntity;
import cn.ibani.redis.annotation.RedisId;
@RedisEntity(name="user")
public class User implements Serializable{

    private static final long serialVersionUID = 1L;

    private Long userId;
    
    private String userName;
    
    private String email;
    
    private Date birthday;
    
    private String passwd;
    
    private String salt;

    public User(){}

    public User( String userName, String email, Date birthday,
            String passwd, String salt) {
        this.userName = userName;
        this.email = email;
        this.birthday = birthday;
        this.passwd = passwd;
        this.salt = salt;
        
    }

    @RedisId
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    @RedisColumn(unique=true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }


    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", userName=" + userName + ", email="
                + email + ", birthday=" + birthday + ", passwd=" + passwd
                + ", salt=" + salt + "]";
    }
}
