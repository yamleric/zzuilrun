package dao;

import model.User;

import java.util.List;

public interface UserDao {
    User getUserByUsername(String username);
    boolean addUser(User user);
    boolean updateUser(User user);
    boolean deleteUser(int userId);
    boolean validateUser(String username, String password);
    boolean changeUserStatus(int userId, int status);

    // 添加缺失的方法声明
    List<User> getAllUsers();
    User getUserById(int userId);

}