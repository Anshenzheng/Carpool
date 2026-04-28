package com.carpool.controller;

import com.carpool.service.ExportService;
import com.carpool.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/statistics")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    @Autowired
    private ExportService exportService;
    
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview() {
        Map<String, Object> statistics = statisticsService.getOverviewStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/routes")
    public ResponseEntity<List<Map<String, Object>>> getRouteStatistics() {
        List<Map<String, Object>> statistics = statisticsService.getRouteStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/export/posts")
    public ResponseEntity<byte[]> exportPosts() {
        try {
            byte[] excelData = exportService.exportPosts();
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=carpool_posts.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/export/applications")
    public ResponseEntity<byte[]> exportApplications() {
        try {
            byte[] excelData = exportService.exportApplications();
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=carpool_applications.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
