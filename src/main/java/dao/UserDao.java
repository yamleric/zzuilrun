package dao;

import model.User;

public interface UserDao {
    User findByUsername(String username);
    boolean save(User user);
    boolean validateUser(String username, String password);
}