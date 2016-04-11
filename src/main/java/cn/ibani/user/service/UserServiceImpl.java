package cn.ibani.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.ibani.redis.exception.RedisException;
import cn.ibani.user.bean.User;
import cn.ibani.user.dao.UserDao;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public boolean save(User user) {
        try {
            userDao.save(user);
        } catch (RedisException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    @Override
    public boolean delete(User user) {
        try {
            userDao.delete(user);
        } catch (RedisException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    @Override
    public boolean update(User oldUser, User newUser) {
        try {
            userDao.update(oldUser, newUser);
        } catch (RedisException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    @Override
    public User queryUserByEmail(String email) {
        User user = null;
        try {
            user = userDao.oneQuery("email", email);
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return user;
    }
    
    

}
