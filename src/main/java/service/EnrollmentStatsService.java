// 新建服务类：EnrollmentStatsService.java
package service;

import dao.EnrollmentStatsDao;
import dao.EnrollmentStatsDaoImpl;
import model.EnrollmentStats;

import java.util.List;

public class EnrollmentStatsService {
    private final EnrollmentStatsDao statsDao = new EnrollmentStatsDaoImpl();

    public List<EnrollmentStats> getEventEnrollmentStats() {
        return statsDao.getEventEnrollmentStats();
    }

    public List<EnrollmentStats> getCollegeEnrollmentStats() {
        return statsDao.getCollegeEnrollmentStats();
    }
}