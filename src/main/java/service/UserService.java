package service;

import dao.UserDaoImpl;
import model.User;
import util.SecurityUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UserService {
    private final UserDaoImpl userDao = new UserDaoImpl();

    public boolean register(User user) {
        // 1. 验证学号格式
        if (!user.getUsername().matches("^\\d{12}$")) {
            JOptionPane.showMessageDialog(null, "学号格式不正确");
            return false;
        }

        // 2. 保存用户
        try {
            user.setPassword(SecurityUtil.hashPassword(user.getPassword()));
            return userDao.addUser(user);
        } catch (Exception e) {
            return false;
        }
    }


    // 在 UserService 类中添加
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    public boolean updateUser(User user) {
        return userDao.updateUser(user);
    }
    public boolean addUser(User user) {
        // 用户名唯一性检查
        if (userDao.getUserByUsername(user.getUsername()) != null) {
            JOptionPane.showMessageDialog(null, "用户名已存在");
            return false;
        }

        // 加密密码
        user.setPassword(SecurityUtil.hashPassword(user.getPassword()));
        return userDao.addUser(user);
    }

    public boolean deleteUser(int userId) {
        return userDao.deleteUser(userId);
    }

    public boolean changeUserStatus(int userId, int status) {
        return userDao.changeUserStatus(userId, status);
    }

    public User getUserById(int userId) {
        return userDao.getUserById(userId);
    }




    public User login(String username, String password) {
        User user = userDao.getUserByUsername(username);
        if (user != null && SecurityUtil.checkPassword(password, user.getPassword())) {
            if (user.getStatus() == 0) {
                throw new RuntimeException("账号已被禁用");
            }
            return user;
        }
        return null;
    }
}

//public class UserService {
//    private UserDao userDao = new UserDao();
//
//    // 用户注册
//    public boolean register(User user) {
//        // 检查用户名是否已存在
//        if (userDao.getUserByUsername(user.getUsername()) != null) {
//            return false;
//        }
//        return userDao.register(user);
//    }
//
//    // 用户登录
//    public User login(String username, String password) {
//        return userDao.login(username, password);
//    }
//
//    // 更新用户信息
//    public boolean updateUser(User user) {
//        return userDao.updateUser(user);
//    }
//
//    // 获取所有用户（管理员用）
//    public List<User> getAllUsers() {
//        return userDao.getAllUsers();
//    }
//
//    // 修改用户状态（启用/禁用）
//    public boolean changeUserStatus(int userId, int status) {
//        return userDao.changeUserStatus(userId, status);
//    }
//}

//    public String register(User user) {
//        // 1. 验证学号格式
//        if (!user.getUsername().matches("^\\d{10}$")) {
//            return "学号格式不正确（需10位数字）";
//        }
//
//        // 2. 密码强度验证
//        if (!SecurityUtil.isPasswordValid(user.getPassword())) {
//            return "密码需包含字母和数字，至少8位";
//        }
//
//        // 3. 加密密码
//        user.setPassword(SecurityUtil.hashPassword(user.getPassword()));
//
//        // 4. 保存用户
//        return userDao.addUser(user) ? "注册成功" : "注册失败";
//    }