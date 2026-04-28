package com.carpool.service;

import com.carpool.entity.CarpoolApplication;
import com.carpool.entity.CarpoolPost;
import com.carpool.repository.CarpoolApplicationRepository;
import com.carpool.repository.CarpoolPostRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportService {
    
    @Autowired
    private CarpoolPostRepository postRepository;
    
    @Autowired
    private CarpoolApplicationRepository applicationRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public byte[] exportPosts() throws IOException {
        List<CarpoolPost> posts = postRepository.findAll();
        
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("拼车信息");
            
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "发布人", "出发地", "目的地", "出发时间", "总座位数", "可用座位", "状态", "审核状态", "发布时间"};
            createHeaderRow(workbook, headerRow, headers);
            
            int rowNum = 1;
            for (CarpoolPost post : posts) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(post.getId());
                row.createCell(1).setCellValue(post.getUser().getRealName());
                row.createCell(2).setCellValue(post.getDeparture());
                row.createCell(3).setCellValue(post.getDestination());
                row.createCell(4).setCellValue(post.getDepartureTime() != null ? post.getDepartureTime().format(DATE_FORMATTER) : "");
                row.createCell(5).setCellValue(post.getSeats());
                row.createCell(6).setCellValue(post.getAvailableSeats());
                row.createCell(7).setCellValue(getStatusText(post.getStatus()));
                row.createCell(8).setCellValue(getAuditStatusText(post.getAuditStatus()));
                row.createCell(9).setCellValue(post.getCreatedAt() != null ? post.getCreatedAt().format(DATE_FORMATTER) : "");
            }
            
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    public byte[] exportApplications() throws IOException {
        List<CarpoolApplication> applications = applicationRepository.findAll();
        
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("拼车申请");
            
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "拼车信息ID", "出发地-目的地", "申请人", "申请人数", "状态", "申请时间", "确认时间"};
            createHeaderRow(workbook, headerRow, headers);
            
            int rowNum = 1;
            for (CarpoolApplication app : applications) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(app.getId());
                row.createCell(1).setCellValue(app.getPost().getId());
                row.createCell(2).setCellValue(app.getPost().getDeparture() + " -> " + app.getPost().getDestination());
                row.createCell(3).setCellValue(app.getApplicantName());
                row.createCell(4).setCellValue(app.getPassengers());
                row.createCell(5).setCellValue(getApplicationStatusText(app.getStatus()));
                row.createCell(6).setCellValue(app.getCreatedAt() != null ? app.getCreatedAt().format(DATE_FORMATTER) : "");
                row.createCell(7).setCellValue(app.getConfirmedAt() != null ? app.getConfirmedAt().format(DATE_FORMATTER) : "");
            }
            
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    private void createHeaderRow(Workbook workbook, Row headerRow, String[] headers) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }
    
    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "待审核";
            case 1 -> "已发布";
            case 2 -> "已下架";
            case 3 -> "已取消";
            default -> "未知";
        };
    }
    
    private String getAuditStatusText(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "待审核";
            case 1 -> "审核通过";
            case 2 -> "审核拒绝";
            default -> "未知";
        };
    }
    
    private String getApplicationStatusText(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "待确认";
            case 1 -> "已确认";
            case 2 -> "已拒绝";
            case 3 -> "已取消";
            default -> "未知";
        };
    }
}
