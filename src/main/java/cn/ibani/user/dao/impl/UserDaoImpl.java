package cn.ibani.user.dao.impl;

import org.springframework.stereotype.Service;

import cn.ibani.redis.dao.impl.RedisDaoImpl;
import cn.ibani.user.bean.User;
import cn.ibani.user.dao.UserDao;
@Service("userDao")
public class UserDaoImpl extends RedisDaoImpl<User>  implements UserDao{


}
