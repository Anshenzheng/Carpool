package com.carpool.repository;

import com.carpool.entity.CarpoolPost;
import com.carpool.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CarpoolPostRepository extends JpaRepository<CarpoolPost, Long> {
    List<CarpoolPost> findByUserOrderByDepartureTimeDesc(User user);
    
    @Query("SELECT p FROM CarpoolPost p WHERE p.status = 1 AND p.departureTime >= :now ORDER BY p.departureTime ASC")
    List<CarpoolPost> findActivePosts(@Param("now") LocalDateTime now);
    
    @Query("SELECT p FROM CarpoolPost p WHERE p.status = 1 AND p.departureTime >= :now " +
           "AND (:departure IS NULL OR p.departure LIKE %:departure%) " +
           "AND (:destination IS NULL OR p.destination LIKE %:destination%) " +
           "ORDER BY p.departureTime ASC")
    List<CarpoolPost> searchActivePosts(
        @Param("departure") String departure,
        @Param("destination") String destination,
        @Param("now") LocalDateTime now
    );
    
    @Query("SELECT p FROM CarpoolPost p WHERE p.auditStatus = 0 ORDER BY p.createdAt ASC")
    List<CarpoolPost> findPendingAudit();
    
    @Query("SELECT COUNT(p) FROM CarpoolPost p WHERE p.departureTime BETWEEN :start AND :end")
    long countByDepartureTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(p) FROM CarpoolPost p WHERE p.status = 1")
    long countActive();
    
    @Query("SELECT p.departure, COUNT(p) as cnt FROM CarpoolPost p GROUP BY p.departure ORDER BY cnt DESC")
    List<Object[]> countByDeparture();
    
    @Query("SELECT p.destination, COUNT(p) as cnt FROM CarpoolPost p GROUP BY p.destination ORDER BY cnt DESC")
    List<Object[]> countByDestination();
}
