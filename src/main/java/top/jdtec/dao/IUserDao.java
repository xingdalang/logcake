package top.jdtec.dao;

import top.jdtec.entity.User;

import java.util.List;

public interface IUserDao {

    public void saveUser(User user);


    public List<User> findUser(User user);
}
