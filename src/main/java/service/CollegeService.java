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
        return collegeDao.getAllColleges();
    }

    public int getCollegeIdByName(String collegeName) {
        return collegeDao.getCollegeIdByName(collegeName);
    }

    public boolean addCollege(String collegeName) {
        return collegeDao.addCollege(collegeName);
    }

    public boolean updateCollege(String oldName, String newName) {
        return collegeDao.updateCollege(oldName, newName);
    }

    public boolean deleteCollege(String collegeName) {
        return collegeDao.deleteCollege(collegeName);
    }
}