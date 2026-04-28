package com.carpool.service;

import com.carpool.dto.CarpoolPostRequest;
import com.carpool.entity.CarpoolPost;
import com.carpool.entity.User;
import com.carpool.repository.CarpoolPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CarpoolPostService {
    
    @Autowired
    private CarpoolPostRepository carpoolPostRepository;
    
    @Autowired
    private AuthService authService;
    
    @Transactional
    public CarpoolPost createPost(CarpoolPostRequest request) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        
        if (request.getDepartureTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("出发时间不能早于当前时间");
        }
        
        CarpoolPost post = new CarpoolPost();
        post.setUser(currentUser);
        post.setDeparture(request.getDeparture());
        post.setDestination(request.getDestination());
        post.setDepartureTime(request.getDepartureTime());
        post.setSeats(request.getSeats());
        post.setAvailableSeats(request.getSeats());
        post.setContactName(request.getContactName());
        post.setContactPhone(request.getContactPhone());
        post.setDescription(request.getDescription());
        post.setStatus(0);
        post.setAuditStatus(0);
        
        return carpoolPostRepository.save(post);
    }
    
    public List<CarpoolPost> getMyPosts() {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        return carpoolPostRepository.findByUserOrderByDepartureTimeDesc(currentUser);
    }
    
    public List<CarpoolPost> getActivePosts() {
        return carpoolPostRepository.findActivePosts(LocalDateTime.now());
    }
    
    public List<CarpoolPost> searchPosts(String departure, String destination) {
        return carpoolPostRepository.searchActivePosts(departure, destination, LocalDateTime.now());
    }
    
    public CarpoolPost getPostById(Long id) {
        return carpoolPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("拼车信息不存在"));
    }
    
    @Transactional
    public CarpoolPost cancelMyPost(Long postId) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        
        CarpoolPost post = carpoolPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("拼车信息不存在"));
        
        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("只能取消自己发布的拼车信息");
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cancelDeadline = post.getDepartureTime().minusMinutes(30);
        
        if (now.isAfter(cancelDeadline)) {
            throw new RuntimeException("出发前30分钟内不能取消拼车");
        }
        
        post.setStatus(3);
        return carpoolPostRepository.save(post);
    }
}
