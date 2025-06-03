package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 院系数据访问对象接口
 */
public interface CollegeDao {
    List<String> getAllCollegeNames();
    boolean addCollege(String collegeName);
    boolean deleteCollegeByName(String collegeName);
}