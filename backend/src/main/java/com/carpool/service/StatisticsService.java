package com.carpool.service;

import com.carpool.repository.CarpoolApplicationRepository;
import com.carpool.repository.CarpoolPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
public class StatisticsService {
    
    @Autowired
    private CarpoolPostRepository postRepository;
    
    @Autowired
    private CarpoolApplicationRepository applicationRepository;
    
    public Map<String, Object> getOverviewStatistics() {
        Map<String, Object> result = new LinkedHashMap<>();
        
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        
        LocalDateTime startOfMonth = YearMonth.now().atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = YearMonth.now().plusMonths(1).atDay(1).atStartOfDay();
        
        result.put("totalPosts", postRepository.count());
        result.put("activePosts", postRepository.countActive());
        result.put("totalApplications", applicationRepository.count());
        result.put("confirmedApplications", applicationRepository.countConfirmed());
        
        result.put("todayPosts", postRepository.countByDepartureTimeBetween(startOfDay, endOfDay));
        result.put("todayApplications", applicationRepository.countConfirmedByDepartureTimeBetween(startOfDay, endOfDay));
        
        result.put("monthPosts", postRepository.countByDepartureTimeBetween(startOfMonth, endOfMonth));
        result.put("monthApplications", applicationRepository.countConfirmedByDepartureTimeBetween(startOfMonth, endOfMonth));
        
        long totalPostsWithConfirm = applicationRepository.countPostsWithConfirmedApplications();
        long totalPosts = postRepository.countActive();
        double completionRate = totalPosts > 0 ? (double) totalPostsWithConfirm / totalPosts * 100 : 0;
        result.put("completionRate", String.format("%.2f%%", completionRate));
        
        return result;
    }
    
    public List<Map<String, Object>> getRouteStatistics() {
        List<Map<String, Object>> result = new ArrayList<>();
        
        List<Object[]> departureStats = postRepository.countByDeparture();
        List<Object[]> destinationStats = postRepository.countByDestination();
        
        Map<String, Long> departureMap = new LinkedHashMap<>();
        Map<String, Long> destinationMap = new LinkedHashMap<>();
        
        for (Object[] row : departureStats) {
            departureMap.put((String) row[0], ((Number) row[1]).longValue());
        }
        
        for (Object[] row : destinationStats) {
            destinationMap.put((String) row[0], ((Number) row[1]).longValue());
        }
        
        Map<String, Object> departureResult = new LinkedHashMap<>();
        departureResult.put("type", "departure");
        departureResult.put("data", departureMap);
        result.add(departureResult);
        
        Map<String, Object> destinationResult = new LinkedHashMap<>();
        destinationResult.put("type", "destination");
        destinationResult.put("data", destinationMap);
        result.add(destinationResult);
        
        return result;
    }
}
