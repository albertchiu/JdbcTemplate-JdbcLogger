package idv.ccw.dao;

import java.util.List;

import idv.ccw.model.User;

public interface UserDao {

    void add(User user);

    void update(User user);

    void delete(int userId);

    List<User> findAll();

    User findOne(int userId);
}
