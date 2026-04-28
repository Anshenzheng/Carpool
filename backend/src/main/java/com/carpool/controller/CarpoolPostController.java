package com.carpool.controller;

import com.carpool.dto.CarpoolPostRequest;
import com.carpool.entity.CarpoolPost;
import com.carpool.service.CarpoolPostService;
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
public class CarpoolPostController {
    
    @Autowired
    private CarpoolPostService carpoolPostService;
    
    @GetMapping("/public/list")
    public ResponseEntity<List<CarpoolPost>> getActivePosts() {
        List<CarpoolPost> posts = carpoolPostService.getActivePosts();
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/public/search")
    public ResponseEntity<List<CarpoolPost>> searchPosts(
            @RequestParam(required = false) String departure,
            @RequestParam(required = false) String destination) {
        List<CarpoolPost> posts = carpoolPostService.searchPosts(departure, destination);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/public/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        try {
            CarpoolPost post = carpoolPostService.getPostById(id);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@Valid @RequestBody CarpoolPostRequest request) {
        try {
            CarpoolPost post = carpoolPostService.createPost(request);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "发布失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/posts/my")
    public ResponseEntity<List<CarpoolPost>> getMyPosts() {
        List<CarpoolPost> posts = carpoolPostService.getMyPosts();
        return ResponseEntity.ok(posts);
    }
    
    @PutMapping("/posts/{id}/cancel")
    public ResponseEntity<?> cancelMyPost(@PathVariable Long id) {
        try {
            CarpoolPost post = carpoolPostService.cancelMyPost(id);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "取消失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
