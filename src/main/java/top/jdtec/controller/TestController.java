package top.jdtec.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.jdtec.dao.IUserDao;
import top.jdtec.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
    @Autowired
    IUserDao userDao;

    @RequestMapping(value = "/t")
    @ResponseBody
    public Object testLog(String a){
        System.out.println("receive:"+a);
        logger.info("receive:"+a);
        return a;
    }

    @RequestMapping(value = "/save")
    @ResponseBody
    public Object saveUser(String name){
        User u = new User();
        u.setName(name);
        userDao.saveUser(u);
        return null;
    }

    @RequestMapping(value = "/get")
    @ResponseBody
    public Object getUser(String name){
        User u = new User();
        u.setName(name);
        List<User> user = userDao.findUser(u);
        return user;
    }
}
