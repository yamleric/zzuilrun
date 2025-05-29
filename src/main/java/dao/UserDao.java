package dao;

import model.User;

public interface UserDao {
    User getUserByUsername(String username);
    boolean addUser(User user);
    boolean updateUser(User user);
    boolean deleteUser(int userId);
    boolean validateUser(String username, String password);
    boolean changeUserStatus(int userId, int status);
}