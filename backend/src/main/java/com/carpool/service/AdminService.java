package com.carpool.service;

import com.carpool.dto.AuditRequest;
import com.carpool.entity.CarpoolPost;
import com.carpool.entity.User;
import com.carpool.repository.CarpoolPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminService {
    
    @Autowired
    private CarpoolPostRepository postRepository;
    
    @Autowired
    private AuthService authService;
    
    public List<CarpoolPost> getPendingAuditPosts() {
        return postRepository.findPendingAudit();
    }
    
    public List<CarpoolPost> getAllPosts() {
        return postRepository.findAll();
    }
    
    @Transactional
    public CarpoolPost auditPost(AuditRequest request) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        
        CarpoolPost post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("拼车信息不存在"));
        
        if (request.getAuditStatus() != 1 && request.getAuditStatus() != 2) {
            throw new RuntimeException("无效的审核状态");
        }
        
        post.setAuditStatus(request.getAuditStatus());
        post.setAuditRemark(request.getAuditRemark());
        post.setAuditor(currentUser);
        post.setAuditTime(LocalDateTime.now());
        
        if (request.getAuditStatus() == 1) {
            post.setStatus(1);
        }
        
        return postRepository.save(post);
    }
    
    @Transactional
    public CarpoolPost takeDownPost(Long postId, String reason) {
        CarpoolPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("拼车信息不存在"));
        
        post.setStatus(2);
        post.setAuditRemark(reason);
        
        return postRepository.save(post);
    }
    
    @Transactional
    public void removeExpiredPosts() {
        LocalDateTime now = LocalDateTime.now();
        List<CarpoolPost> allPosts = postRepository.findAll();
        
        for (CarpoolPost post : allPosts) {
            if (post.getDepartureTime().isBefore(now) && post.getStatus() == 1) {
                post.setStatus(2);
                post.setAuditRemark("系统自动下架：已过期");
                postRepository.save(post);
            }
        }
    }
}
