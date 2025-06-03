package service;

import dao.CollegeDao;
import dao.CollegeDaoImpl;
import util.DatabaseUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CollegeService {
    private final CollegeDao collegeDao = new CollegeDaoImpl();

    public List<String> getAllColleges() {
        return collegeDao.getAllCollegeNames();
    }

    public boolean addCollege(String collegeName) {
        return collegeDao.addCollege(collegeName);
    }

    public boolean deleteCollege(String collegeName) {
        return collegeDao.deleteCollegeByName(collegeName);
    }

    // 如果需要此方法
    public int getCollegeIdByName(String name) {
        return collegeDao instanceof CollegeDaoImpl ?
                ((CollegeDaoImpl) collegeDao).getCollegeIdByName(name) : -1;
    }
    // 新增：检查院系是否可以安全删除
    public boolean canDeleteCollege(String collegeName) {
        // MVP阶段简化处理：总是返回true
        return true;
    }
}