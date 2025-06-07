package service;

import model.ArrangementGroup;
import model.Enrollment;
import model.Event;
import model.TrackAssignment;
import model.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.DatabaseUtil;

import java.awt.*;
//import java.awt.Font;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class ArrangementService {
    private static final int GROUP_SIZE = 8; // 每组最多8人
    private static final int[] LANE_ORDER = {4, 5, 3, 6, 2, 7, 1, 8}; // 国际田联标准道次分配

    // 获取需要编排的项目（报名已结束且未编排）
    public List<Event> getEventsForArrangement() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE status = 2 AND arrangement_status = 0";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Event event = new Event();
                event.setEventId(rs.getInt("event_id"));
                event.setEventName(rs.getString("event_name"));
                event.setEventType(rs.getInt("event_type"));
                event.setGenderLimit(rs.getString("gender_limit"));
                event.setMinParticipants(rs.getInt("min_participants"));
                event.setMaxParticipants(rs.getInt("max_participants"));

                Timestamp start = rs.getTimestamp("start_time");
                Timestamp end = rs.getTimestamp("end_time");
                if (start != null) event.setStartTime(start.toLocalDateTime());
                if (end != null) event.setEndTime(end.toLocalDateTime());

                event.setLocation(rs.getString("location"));
                event.setDescription(rs.getString("description"));
                event.setStatus(rs.getInt("status"));

                events.add(event);
            }
        } catch (SQLException e) {
            System.err.println("获取待编排项目失败: " + e.getMessage());
            e.printStackTrace();
        }
        return events;
    }

    // 获取项目的已通过报名
    public List<Enrollment> getApprovedEnrollments(int eventId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT r.registration_id, r.user_id, u.real_name, u.college_id " +
                "FROM registrations r " +
                "JOIN users u ON r.user_id = u.user_id " +
                "WHERE r.event_id = ? AND r.status = 1";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, eventId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Enrollment enrollment = new Enrollment();
                    enrollment.setEnrollmentId(rs.getInt("registration_id"));
                    enrollment.setUserId(rs.getInt("user_id"));

                    // 设置用户信息
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setRealName(rs.getString("real_name"));
                    user.setCollegeId(rs.getInt("college_id"));
                    enrollment.setUser(user);

                    enrollments.add(enrollment);
                }
            }
        } catch (SQLException e) {
            System.err.println("获取已通过报名失败: " + e.getMessage());
            e.printStackTrace();
        }
        return enrollments;
    }

    // 自动分组算法
    public List<ArrangementGroup> autoGroup(Event event, List<Enrollment> enrollments) {
        List<ArrangementGroup> groups = new ArrayList<>();

        // 1. 随机排序选手（MVP阶段简化实现）
        Collections.shuffle(enrollments);

        // 2. 蛇形分组（保证每组实力均衡）
        int groupCount = (int) Math.ceil((double) enrollments.size() / GROUP_SIZE);
        List<List<Enrollment>> groupedParticipants = new ArrayList<>();

        // 初始化空分组
        for (int i = 0; i < groupCount; i++) {
            groupedParticipants.add(new ArrayList<>());
        }

        int index = 0;
        boolean forward = true;
        for (Enrollment enrollment : enrollments) {
            groupedParticipants.get(index).add(enrollment);

            if (forward) {
                if (index == groupCount - 1) {
                    forward = false;
                } else {
                    index++;
                }
            } else {
                if (index == 0) {
                    forward = true;
                } else {
                    index--;
                }
            }
        }

        // 3. 创建分组对象并分配道次
        for (int i = 0; i < groupCount; i++) {
            ArrangementGroup group = new ArrangementGroup();
            group.setEvent(event);
            group.setGroupName("第" + (i+1) + "组");
            group.setGroupType("预赛");
            group.setParticipants(groupedParticipants.get(i));

            // 分配道次 - 使用分组索引作为赛道号
            group.setAssignments(assignTracks(group, i));

            groups.add(group);
        }

        return groups;
    }

    // 自动分配道次（修复：添加分组索引参数）
    public List<TrackAssignment> assignTracks(ArrangementGroup group, int groupIndex) {
        List<TrackAssignment> assignments = new ArrayList<>();
        int track = groupIndex + 1; // 赛道号（组号）

        List<Enrollment> participants = group.getParticipants();
        for (int i = 0; i < participants.size(); i++) {
            TrackAssignment assignment = new TrackAssignment();
            assignment.setEnrollment(participants.get(i));
            assignment.setTrackNumber(track);
            assignment.setLaneNumber(LANE_ORDER[i]); // 使用标准道次顺序

            assignments.add(assignment);
        }

        return assignments;
    }

    // 自动分配道次（重载方法，兼容旧调用）
    public List<TrackAssignment> assignTracks(ArrangementGroup group) {
        // 默认使用0作为索引（实际场景应避免使用）
        return assignTracks(group, 0);
    }

    // 保存编排结果到数据库
    public void saveArrangement(List<ArrangementGroup> groups) {
        if (groups == null || groups.isEmpty()) {
            throw new IllegalArgumentException("分组数据不能为空");
        }

        String groupSql = "INSERT INTO event_groups (event_id, group_name, group_type, group_order, start_time, track_count, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String assignmentSql = "INSERT INTO participant_groups (registration_id, group_id, track_number, lane_number) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false); // 开始事务

            // 添加：先删除该事件的所有旧分组数据
            try (PreparedStatement deleteAssignStmt = conn.prepareStatement(
                    "DELETE FROM participant_groups WHERE group_id IN " +
                            "(SELECT group_id FROM event_groups WHERE event_id = ?)")) {
                deleteAssignStmt.setInt(1, groups.get(0).getEvent().getEventId());
                deleteAssignStmt.executeUpdate();
            }

            try (PreparedStatement deleteGroupStmt = conn.prepareStatement(
                    "DELETE FROM event_groups WHERE event_id = ?")) {
                deleteGroupStmt.setInt(1, groups.get(0).getEvent().getEventId());
                deleteGroupStmt.executeUpdate();
            }


            try (PreparedStatement groupStmt = conn.prepareStatement(groupSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement assignmentStmt = conn.prepareStatement(assignmentSql)) {

                // 1. 保存分组信息
                for (int i = 0; i < groups.size(); i++) {
                    ArrangementGroup group = groups.get(i);

                    groupStmt.setInt(1, group.getEvent().getEventId());
                    groupStmt.setString(2, group.getGroupName());
                    groupStmt.setString(3, group.getGroupType());
                    groupStmt.setInt(4, i + 1); // 组顺序

                    // 设置比赛时间（如果未设置，使用项目开始时间）
                    LocalDateTime startTime = group.getStartTime() != null ?
                            group.getStartTime() : group.getEvent().getStartTime();
                    groupStmt.setTimestamp(5, Timestamp.valueOf(startTime));

                    groupStmt.setInt(6, group.getParticipants().size());
                    groupStmt.setInt(7, 1); // 状态：进行中

                    groupStmt.executeUpdate();

                    // 获取生成的group_id
                    try (ResultSet rs = groupStmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            int groupId = rs.getInt(1);

                            // 2. 保存道次分配信息
                            for (TrackAssignment assignment : group.getAssignments()) {
                                assignmentStmt.setInt(1, assignment.getEnrollment().getEnrollmentId());
                                assignmentStmt.setInt(2, groupId);
                                assignmentStmt.setInt(3, assignment.getTrackNumber());
                                assignmentStmt.setInt(4, assignment.getLaneNumber());
                                assignmentStmt.addBatch();
                            }
                        }
                    }
                }

                // 执行批量插入
                assignmentStmt.executeBatch();

                // 3. 更新项目状态为"编排完成"
                updateEventStatus(groups.get(0).getEvent().getEventId(), 3, conn);

                conn.commit(); // 提交事务
                System.out.println("编排结果保存成功，影响分组数: " + groups.size());
            } catch (SQLException e) {
                try {
                    conn.rollback(); // 回滚事务
                } catch (SQLException ex) {
                    System.err.println("回滚事务失败: " + ex.getMessage());
                }
                throw new RuntimeException("保存编排结果失败: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("数据库操作失败: " + e.getMessage(), e);
        }
    }

    // 生成比赛名单PDF（简化版实现）
    public void generateStartList(Event event) {
        // TODO: 实际项目中应使用iText或Apache PDFBox生成PDF
        // 这里仅打印信息到控制台作为演示

        System.out.println("=== 比赛名单 - " + event.getEventName() + " ===");
        System.out.println("比赛时间: " + event.getStartTime());
        System.out.println("比赛地点: " + event.getLocation());
        System.out.println();

        // 获取该项目所有分组 - 只获取当前状态的分组
        String groupSql = "SELECT * FROM event_groups WHERE event_id = ? AND status = 1";
        String assignmentSql = "SELECT u.real_name, c.college_name, pg.track_number, pg.lane_number " +
                "FROM participant_groups pg " +
                "JOIN registrations r ON pg.registration_id = r.registration_id " +
                "JOIN users u ON r.user_id = u.user_id " +
                "JOIN colleges c ON u.college_id = c.college_id " +
                "WHERE pg.group_id = ? " +
                "ORDER BY pg.lane_number";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement groupStmt = conn.prepareStatement(groupSql);
             PreparedStatement assignStmt = conn.prepareStatement(assignmentSql)) {

            groupStmt.setInt(1, event.getEventId());
            try (ResultSet groupRs = groupStmt.executeQuery()) {
                while (groupRs.next()) {
                    String groupName = groupRs.getString("group_name");
                    if (!groupName.contains("第") || !groupName.contains("组")) {
                        groupName = "第" + groupName + "组";
                    }

                    System.out.println("--- " + groupRs.getString("group_name") + " ---");
                    System.out.println("比赛时间: " + groupRs.getTimestamp("start_time").toLocalDateTime());
                    System.out.println("=====================================");
                    System.out.printf("%-15s %-15s %-15s %-15s%n", "姓名", "学院", "赛道", "道次");

                    assignStmt.setInt(1, groupRs.getInt("group_id"));
                    try (ResultSet assignRs = assignStmt.executeQuery()) {
                        while (assignRs.next()) {
                            System.out.printf("%-15s %-15s %-15d %-15d%n",
                                    assignRs.getString("real_name"),
                                    assignRs.getString("college_name"),
                                    assignRs.getInt("track_number"),
                                    assignRs.getInt("lane_number"));
                        }
                    }
                    System.out.println("\n");
                }
            }

            // 在真实系统中，这里应该生成PDF文件并返回下载链接
            System.out.println("比赛名单生成完成");
        } catch (SQLException e) {
            System.err.println("生成比赛名单失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 设置分组比赛时间
    public void setGroupTime(ArrangementGroup group, LocalDateTime startTime) {
        group.setStartTime(startTime);
    }

    // 更新项目编排状态
    private void updateEventStatus(int eventId, int status, Connection conn) throws SQLException {
        String sql = "UPDATE events SET arrangement_status = ? WHERE event_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, status);
            stmt.setInt(2, eventId);
            stmt.executeUpdate();
        }
    }

    // 获取分组详细信息（用于UI展示）
    public List<ArrangementGroup> getEventGroups(int eventId) {
        List<ArrangementGroup> groups = new ArrayList<>();

        String groupSql = "SELECT * FROM event_groups WHERE event_id = ? ORDER BY group_order";

        // 修复后的assignmentSql（添加了r.user_id）
        String assignmentSql = "SELECT pg.*, r.user_id, u.real_name, u.college_id " +
                "FROM participant_groups pg " +
                "JOIN registrations r ON pg.registration_id = r.registration_id " +
                "JOIN users u ON r.user_id = u.user_id " +
                "WHERE pg.group_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement groupStmt = conn.prepareStatement(groupSql);
             PreparedStatement assignStmt = conn.prepareStatement(assignmentSql)) {

            groupStmt.setInt(1, eventId);
            try (ResultSet groupRs = groupStmt.executeQuery()) {
                while (groupRs.next()) {
                    ArrangementGroup group = new ArrangementGroup();
                    group.setGroupId(groupRs.getInt("group_id"));
                    group.setGroupName(groupRs.getString("group_name"));
                    group.setGroupType(groupRs.getString("group_type"));
                    group.setStartTime(groupRs.getTimestamp("start_time").toLocalDateTime());

                    // 获取分组内的道次分配
                    List<TrackAssignment> assignments = new ArrayList<>();
                    assignStmt.setInt(1, group.getGroupId());
                    try (ResultSet assignRs = assignStmt.executeQuery()) {
                        while (assignRs.next()) {
                            TrackAssignment assignment = new TrackAssignment();
                            assignment.setTrackNumber(assignRs.getInt("track_number"));
                            assignment.setLaneNumber(assignRs.getInt("lane_number"));

                            // 设置报名信息
                            Enrollment enrollment = new Enrollment();
                            enrollment.setEnrollmentId(assignRs.getInt("registration_id"));

                            // 设置用户信息
                            User user = new User();
                            // 现在可以获取到user_id了
                            user.setUserId(assignRs.getInt("user_id"));
                            user.setRealName(assignRs.getString("real_name"));
                            user.setCollegeId(assignRs.getInt("college_id"));
                            enrollment.setUser(user);

                            assignment.setEnrollment(enrollment);
                            assignments.add(assignment);
                        }
                    }
                    group.setAssignments(assignments);
                    groups.add(group);
                }
            }
        } catch (SQLException e) {
            System.err.println("获取分组信息失败: " + e.getMessage());
            e.printStackTrace();
        }
        return groups;
    }
    public void exportStartListToExcel(Event event, String filePath) throws IOException {
        List<ArrangementGroup> groups = getEventGroups(event.getEventId());

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(event.getEventName());

            // 创建标题
            Row titleRow = sheet.createRow(0);
            titleRow.createCell(0).setCellValue("比赛名单 - " + event.getEventName());
            // 修改合并区域范围，包含赛道列
            CellRangeAddress mergedRegion = new CellRangeAddress(0, 0, 0, 4);
            sheet.addMergedRegion(mergedRegion);

            // 设置标题样式
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleRow.getCell(0).setCellStyle(titleStyle);

            // 添加基本信息
            Row infoRow1 = sheet.createRow(1);
            infoRow1.createCell(0).setCellValue("比赛时间: " + event.getStartTime());
            Row infoRow2 = sheet.createRow(2);
            infoRow2.createCell(0).setCellValue("比赛地点: " + event.getLocation());

            int rowIndex = 4;
            for (ArrangementGroup group : groups) {
                // 统一分组名称格式
                String groupName = group.getGroupName();
                if (!groupName.contains("第") || !groupName.contains("组")) {
                    groupName = "第" + groupName + "组";
                    group.setGroupName(groupName); // 更新对象的分组名称
                }

                // 分组标题
                Row groupTitleRow = sheet.createRow(rowIndex++);
                groupTitleRow.createCell(0).setCellValue(group.getGroupName());

                // 分组信息
                Row groupInfoRow = sheet.createRow(rowIndex++);
                groupInfoRow.createCell(0).setCellValue("组别时间: " + group.getStartTime());

                // 添加赛道信息（关键修改点） - 使用第一个选手的赛道号
                Row trackInfoRow = sheet.createRow(rowIndex++);
                if (!group.getAssignments().isEmpty()) {
                    trackInfoRow.createCell(0).setCellValue("赛道: " + group.getAssignments().get(0).getTrackNumber());
                } else {
                    trackInfoRow.createCell(0).setCellValue("赛道: 未知");
                }

                // 表头（添加"赛道"列）
                Row headerRow = sheet.createRow(rowIndex++);
                headerRow.createCell(0).setCellValue("序号");
                headerRow.createCell(1).setCellValue("姓名");
                headerRow.createCell(2).setCellValue("学院");
                headerRow.createCell(3).setCellValue("赛道"); // 新增赛道列
                headerRow.createCell(4).setCellValue("道次");

                // 表头样式
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                // 更新列数为5列（0-4）
                for (int i = 0; i < 5; i++) {
                    Cell cell = headerRow.getCell(i);
                    if (cell == null) {
                        cell = headerRow.createCell(i);
                    }
                    cell.setCellStyle(headerStyle);
                }

                // 填充参赛选手（添加赛道号）
                int index = 1;
                for (TrackAssignment assignment : group.getAssignments()) {
                    Row dataRow = sheet.createRow(rowIndex++);
                    dataRow.createCell(0).setCellValue(index++); // 序号
                    dataRow.createCell(1).setCellValue(assignment.getEnrollment().getUser().getRealName()); // 姓名
                    dataRow.createCell(2).setCellValue(
                            DatabaseUtil.fetchCollegeName(assignment.getEnrollment().getUser().getCollegeId()) // 学院
                    );
                    dataRow.createCell(3).setCellValue(assignment.getTrackNumber()); // 赛道号（新增）
                    dataRow.createCell(4).setCellValue(assignment.getLaneNumber()); // 道次
                }

                rowIndex++; // 组间留空
            }

            // 自动调整列宽（更新为5列）
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            // 写入文件
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        }
    }
}