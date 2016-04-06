package idv.ccw.service;

import java.util.List;

import idv.ccw.model.User;

public interface UserService {

    List<User> findAll();

    User findOne(int userId);

    void update(User user);
}