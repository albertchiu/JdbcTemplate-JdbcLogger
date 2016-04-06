package idv.ccw.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import idv.ccw.dao.UserDao;
import idv.ccw.jdbc.MyRowMapper;
import idv.ccw.model.User;

@Repository
public class UserDaoImpl extends JdbcDaoSupport implements UserDao {

    @Autowired
    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        super.setJdbcTemplate(jdbcTemplate);
    }

    @Override
    public void add(final User user) {

        final String sql = "insert into User values(?,?,?)";
        PreparedStatementCreator psc = new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, user.getId());
                ps.setString(2, user.getUserName());
                ps.setString(3, user.getEmail());
                return ps;
            }
        };
        getJdbcTemplate().update(psc);
    }

    @Override
    public void update(User user) {
        String sql = "Update User set user_name=? where id=?";
        Object args[] = { user.getUserName(), user.getId() };
        getJdbcTemplate().update(sql, args);
    }

    @Override
    public void delete(int userId) {
        String sql = "delete from User where id=?";
        Object args[] = { userId };
        getJdbcTemplate().update(sql, args);
    }

    @Override
    public List<User> findAll() {
        String sql = "Select * from User";
        return getJdbcTemplate().query(sql, MyRowMapper.newInstance(User.class));
    }

    @Override
    public User findOne(int userId) {
        String sql = "Select * from User where id=?";
        Object args[] = { userId };
        return getJdbcTemplate().queryForObject(sql, args, MyRowMapper.newInstance(User.class));
    }
}