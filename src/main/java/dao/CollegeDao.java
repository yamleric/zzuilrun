package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 院系数据访问对象接口
 */
public interface CollegeDao {
    List<String> getAllColleges();
    int getCollegeIdByName(String collegeName);
    boolean addCollege(String collegeName);
    boolean updateCollege(String oldName, String newName);
    boolean deleteCollege(String collegeName);
}