package cn.ibani.user.service;

import cn.ibani.user.bean.User;


public interface UserService{
    
    public boolean save(User user);
    public boolean delete(User user);
    public boolean update(User oldUser, User newUser);
    public User queryUserByEmail(String email) ;
}
