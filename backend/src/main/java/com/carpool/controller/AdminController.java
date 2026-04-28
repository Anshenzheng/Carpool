package com.carpool.controller;

import com.carpool.dto.AuditRequest;
import com.carpool.entity.CarpoolPost;
import com.carpool.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @GetMapping("/posts/pending")
    public ResponseEntity<List<CarpoolPost>> getPendingAuditPosts() {
        List<CarpoolPost> posts = adminService.getPendingAuditPosts();
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/posts/all")
    public ResponseEntity<List<CarpoolPost>> getAllPosts() {
        List<CarpoolPost> posts = adminService.getAllPosts();
        return ResponseEntity.ok(posts);
    }
    
    @PostMapping("/posts/audit")
    public ResponseEntity<?> auditPost(@Valid @RequestBody AuditRequest request) {
        try {
            CarpoolPost post = adminService.auditPost(request);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "审核失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/posts/{id}/take-down")
    public ResponseEntity<?> takeDownPost(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        try {
            String reason = body != null ? body.get("reason") : "管理员下架";
            CarpoolPost post = adminService.takeDownPost(id, reason);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "下架失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
