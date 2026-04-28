package com.carpool.service;

import com.carpool.dto.CarpoolApplicationRequest;
import com.carpool.entity.CarpoolApplication;
import com.carpool.entity.CarpoolPost;
import com.carpool.entity.User;
import com.carpool.repository.CarpoolApplicationRepository;
import com.carpool.repository.CarpoolPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CarpoolApplicationService {
    
    @Autowired
    private CarpoolApplicationRepository applicationRepository;
    
    @Autowired
    private CarpoolPostRepository postRepository;
    
    @Autowired
    private AuthService authService;
    
    @Transactional
    public CarpoolApplication applyForCarpool(CarpoolApplicationRequest request) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        
        CarpoolPost post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("拼车信息不存在"));
        
        if (post.getStatus() != 1) {
            throw new RuntimeException("该拼车信息不可申请");
        }
        
        if (post.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("不能申请自己发布的拼车");
        }
        
        if (applicationRepository.existsByPostAndUser(post, currentUser)) {
            throw new RuntimeException("您已申请过该拼车");
        }
        
        if (post.getAvailableSeats() < request.getPassengers()) {
            throw new RuntimeException("座位不足");
        }
        
        CarpoolApplication application = new CarpoolApplication();
        application.setPost(post);
        application.setUser(currentUser);
        application.setApplicantName(request.getApplicantName());
        application.setApplicantPhone(request.getApplicantPhone());
        application.setPassengers(request.getPassengers());
        application.setStatus(0);
        
        return applicationRepository.save(application);
    }
    
    public List<CarpoolApplication> getMyApplications() {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        return applicationRepository.findByUserOrderByCreatedAtDesc(currentUser);
    }
    
    public List<CarpoolApplication> getApplicationsForMyPosts() {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        return applicationRepository.findByPostPublisher(currentUser);
    }
    
    @Transactional
    public CarpoolApplication confirmApplication(Long applicationId) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        
        CarpoolApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("申请不存在"));
        
        if (!application.getPost().getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("只能确认自己发布的拼车申请");
        }
        
        if (application.getStatus() != 0) {
            throw new RuntimeException("只能确认待确认的申请");
        }
        
        CarpoolPost post = application.getPost();
        if (post.getAvailableSeats() < application.getPassengers()) {
            throw new RuntimeException("座位不足");
        }
        
        post.setAvailableSeats(post.getAvailableSeats() - application.getPassengers());
        postRepository.save(post);
        
        application.setStatus(1);
        application.setConfirmedAt(LocalDateTime.now());
        
        return applicationRepository.save(application);
    }
    
    @Transactional
    public CarpoolApplication rejectApplication(Long applicationId, String rejectReason) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        
        CarpoolApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("申请不存在"));
        
        if (!application.getPost().getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("只能拒绝自己发布的拼车申请");
        }
        
        if (application.getStatus() != 0) {
            throw new RuntimeException("只能拒绝待确认的申请");
        }
        
        application.setStatus(2);
        application.setRejectReason(rejectReason);
        
        return applicationRepository.save(application);
    }
    
    @Transactional
    public CarpoolApplication cancelMyApplication(Long applicationId) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        
        CarpoolApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("申请不存在"));
        
        if (!application.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("只能取消自己的申请");
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cancelDeadline = application.getPost().getDepartureTime().minusMinutes(30);
        
        if (now.isAfter(cancelDeadline)) {
            throw new RuntimeException("出发前30分钟内不能取消拼车申请");
        }
        
        if (application.getStatus() == 1) {
            CarpoolPost post = application.getPost();
            post.setAvailableSeats(post.getAvailableSeats() + application.getPassengers());
            postRepository.save(post);
        }
        
        application.setStatus(3);
        application.setCancelledAt(LocalDateTime.now());
        
        return applicationRepository.save(application);
    }
}
