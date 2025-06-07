package service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import dao.EventDao;
import dao.EventDaoImpl;
import model.Event;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

//public class EventService {
//    private final EventDao eventDao = new EventDaoImpl();
//
//    public List<Event> getAllEvents() {
//        return eventDao.findAllEvents();
//    }
//
//    public boolean addEvent(Event event) {
//        return eventDao.insertEvent(event) > 0;
//    }
//
//    public boolean updateEvent(Event event) {
//        return eventDao.updateEvent(event);
//    }
//
//    public boolean deleteEvent(int eventId) {
//        return eventDao.deleteEvent(eventId);
//    }
//}
public class EventService {
    private final EventDao eventDao = new EventDaoImpl();

    public int insertEvent(Event event) {
        return eventDao.insertEvent(event);
    }

    public boolean updateEvent(Event event) {
        return eventDao.updateEvent(event);
    }

    public List<Event> getAllEvents() {
        return eventDao.findAllEvents();
    }

    public boolean deleteEvent(int eventId) {
        return eventDao.deleteEvent(eventId);
    }

    // 导入CSV文件
    public void importEventsFromCsv(String filePath) throws IOException, CsvValidationException {
        List<Event> events = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath)).withSkipLines(1).build()) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line.length < 10) continue; // 确保有足够列
                Event event = new Event();
                event.setEventName(line[0]);
                event.setEventType(Integer.parseInt(line[1]));
                event.setGenderLimit(line[2]);
                event.setMinParticipants(Integer.parseInt(line[3]));
                event.setMaxParticipants(Integer.parseInt(line[4]));
                event.setStartTime(LocalDateTime.parse(line[5], formatter));
                event.setEndTime(LocalDateTime.parse(line[6], formatter));
                event.setLocation(line[7]);
                event.setDescription(line[8]);
                event.setStatus(Integer.parseInt(line[9]));
                events.add(event);
            }
        }

        saveEventsToDatabase(events);
    }

    // 导入Excel文件
    public void importEventsFromExcel(String filePath) throws IOException {
        List<Event> events = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try (Workbook workbook = WorkbookFactory.create(new File(filePath))) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // 跳过表头

                Event event = new Event();
                event.setEventName(getStringValue(row.getCell(0)));
                event.setEventType((int) getNumericValue(row.getCell(1)));
                event.setGenderLimit(getStringValue(row.getCell(2)));
                event.setMinParticipants((int) getNumericValue(row.getCell(3)));
                event.setMaxParticipants((int) getNumericValue(row.getCell(4)));
                event.setStartTime(LocalDateTime.parse(getStringValue(row.getCell(5)), formatter));
                event.setEndTime(LocalDateTime.parse(getStringValue(row.getCell(6)), formatter));
                event.setLocation(getStringValue(row.getCell(7)));
                event.setDescription(getStringValue(row.getCell(8)));
                event.setStatus((int) getNumericValue(row.getCell(9)));
                events.add(event);
            }
        }

        saveEventsToDatabase(events);
    }

    // 导出到CSV文件
    public void exportEventsToCsv(String filePath) throws IOException {
        List<Event> events = eventDao.findAllEvents();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // 表头
            writer.writeNext(new String[]{
                    "项目名称", "项目类型", "性别限制", "最小人数", "最大人数",
                    "开始时间", "结束时间", "地点", "描述", "状态"
            });

            // 数据行
            for (Event event : events) {
                writer.writeNext(new String[]{
                        event.getEventName(),
                        String.valueOf(event.getEventType()),
                        event.getGenderLimit(),
                        String.valueOf(event.getMinParticipants()),
                        String.valueOf(event.getMaxParticipants()),
                        event.getStartTime().format(formatter),
                        event.getEndTime().format(formatter),
                        event.getLocation(),
                        event.getDescription(),
                        String.valueOf(event.getStatus())
                });
            }
        }
    }

    // 导出到Excel文件
    public void exportEventsToExcel(String filePath) throws IOException {
        List<Event> events = eventDao.findAllEvents();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("比赛项目");

            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "项目名称", "项目类型", "性别限制", "最小人数", "最大人数",
                    "开始时间", "结束时间", "地点", "描述", "状态"
            };
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // 填充数据
            int rowNum = 1;
            for (Event event : events) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(event.getEventName());
                row.createCell(1).setCellValue(event.getEventType());
                row.createCell(2).setCellValue(event.getGenderLimit());
                row.createCell(3).setCellValue(event.getMinParticipants());
                row.createCell(4).setCellValue(event.getMaxParticipants());
                row.createCell(5).setCellValue(event.getStartTime().format(formatter));
                row.createCell(6).setCellValue(event.getEndTime().format(formatter));
                row.createCell(7).setCellValue(event.getLocation());
                row.createCell(8).setCellValue(event.getDescription());
                row.createCell(9).setCellValue(event.getStatus());
            }

            // 写入文件
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        }
    }

    // 辅助方法：获取单元格值
    private String getStringValue(Cell cell) {
        if (cell == null) return "";
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue();
    }

    private double getNumericValue(Cell cell) {
        if (cell == null) return 0;
        cell.setCellType(CellType.NUMERIC);
        return cell.getNumericCellValue();
    }

    // 保存到数据库
    private void saveEventsToDatabase(List<Event> events) {
        for (Event event : events) {
            if (eventDao.getEventByName(event.getEventName()) == null) {
                eventDao.insertEvent(event);
            } else {
                eventDao.updateEvent(event);
            }
        }
    }
}