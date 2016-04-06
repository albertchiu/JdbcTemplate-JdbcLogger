package idv.ccw.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import idv.ccw.dao.UserDao;
import idv.ccw.model.User;
import idv.ccw.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public User findOne(int userId) {
        return userDao.findOne(userId);
    }

    @Override
    @Transactional
    public void update(User user) {
        userDao.update(user);
    }
}