package cn.ibani.user.service;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.ibani.user.bean.User;

@RunWith(SpringJUnit4ClassRunner.class) 
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class UserServiceTest {

    @Autowired
    private UserService userService;
    
    
    @Test
    public void testSave(){
        User user  = new User("小芳","xiaogou@163.com",new Date(),"123456","salt");
        userService.save(user);
        System.out.println(user);
    }
    
    @Test
    public void testDelete(){
        User user  = new User("小芳","xiaogou@163.com",new Date(),"123456","salt");
        user.setUserId(2l);
        userService.delete(user);
    }
    

    @Test
    public void testUpdate(){
        User userOld  = new User("小芳","xiaoming@163.com",new Date(),"123456","salt");
        userOld.setUserId(2l);
        
        User userNew  = new User("小芳","xiaofang@163.com",new Date(),"123456","salt");
        userNew.setUserId(2l);
        userService.update(userOld,userNew);
    }
    
    @Test
    public void testQuery(){
        User user  = userService.queryUserByEmail("xiaogou@163.com");
        System.out.println(user);
    }
    
}
