package com.carpool.controller;

import com.carpool.dto.CarpoolApplicationRequest;
import com.carpool.entity.CarpoolApplication;
import com.carpool.service.CarpoolApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carpool")
@CrossOrigin(origins = "http://localhost:4200")
public class CarpoolApplicationController {
    
    @Autowired
    private CarpoolApplicationService applicationService;
    
    @PostMapping("/applications")
    public ResponseEntity<?> applyForCarpool(@Valid @RequestBody CarpoolApplicationRequest request) {
        try {
            CarpoolApplication application = applicationService.applyForCarpool(request);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "申请失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/applications/my")
    public ResponseEntity<List<CarpoolApplication>> getMyApplications() {
        List<CarpoolApplication> applications = applicationService.getMyApplications();
        return ResponseEntity.ok(applications);
    }
    
    @GetMapping("/applications/received")
    public ResponseEntity<List<CarpoolApplication>> getApplicationsForMyPosts() {
        List<CarpoolApplication> applications = applicationService.getApplicationsForMyPosts();
        return ResponseEntity.ok(applications);
    }
    
    @PutMapping("/applications/{id}/confirm")
    public ResponseEntity<?> confirmApplication(@PathVariable Long id) {
        try {
            CarpoolApplication application = applicationService.confirmApplication(id);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "确认失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/applications/{id}/reject")
    public ResponseEntity<?> rejectApplication(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        try {
            String rejectReason = body != null ? body.get("rejectReason") : null;
            CarpoolApplication application = applicationService.rejectApplication(id, rejectReason);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "拒绝失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/applications/{id}/cancel")
    public ResponseEntity<?> cancelMyApplication(@PathVariable Long id) {
        try {
            CarpoolApplication application = applicationService.cancelMyApplication(id);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "取消失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
