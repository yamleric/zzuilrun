// 新建DAO接口：EnrollmentStatsDao.java
package dao;

import model.EnrollmentStats;
import java.util.List;

public interface EnrollmentStatsDao {
    List<EnrollmentStats> getEventEnrollmentStats();
    List<EnrollmentStats> getCollegeEnrollmentStats();
}