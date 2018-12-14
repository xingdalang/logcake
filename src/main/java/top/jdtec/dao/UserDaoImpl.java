package top.jdtec.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import top.jdtec.entity.User;

import java.util.List;
@Component
public class UserDaoImpl implements IUserDao{
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public void saveUser(User user) {
        mongoTemplate.save(user);
    }

    @Override
    public List<User> findUser(User user) {
        Query query = new Query(Criteria.where("name").is(user.getName()));
        List<User> users = mongoTemplate.find(query, User.class);
        return users;
    }
}
