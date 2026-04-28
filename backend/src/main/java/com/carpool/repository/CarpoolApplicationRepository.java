package com.carpool.repository;

import com.carpool.entity.CarpoolApplication;
import com.carpool.entity.CarpoolPost;
import com.carpool.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarpoolApplicationRepository extends JpaRepository<CarpoolApplication, Long> {
    List<CarpoolApplication> findByPostOrderByCreatedAtDesc(CarpoolPost post);
    List<CarpoolApplication> findByUserOrderByCreatedAtDesc(User user);
    
    @Query("SELECT a FROM CarpoolApplication a WHERE a.post.user = :publisher ORDER BY a.createdAt DESC")
    List<CarpoolApplication> findByPostPublisher(@Param("publisher") User publisher);
    
    boolean existsByPostAndUser(CarpoolPost post, User user);
    
    Optional<CarpoolApplication> findByPostAndUser(CarpoolPost post, User user);
    
    @Query("SELECT COUNT(a) > 0 FROM CarpoolApplication a WHERE a.post = :post AND a.user = :user AND a.status IN (0, 1, 2)")
    boolean existsActiveApplicationByPostAndUser(@Param("post") CarpoolPost post, @Param("user") User user);
    
    @Query("SELECT a FROM CarpoolApplication a WHERE a.post = :post AND a.user = :user AND a.status IN (0, 1, 2)")
    Optional<CarpoolApplication> findActiveApplicationByPostAndUser(@Param("post") CarpoolPost post, @Param("user") User user);
    
    @Query("SELECT COUNT(a) FROM CarpoolApplication a WHERE a.status = 1 AND a.post.departureTime BETWEEN :start AND :end")
    long countConfirmedByDepartureTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(a) FROM CarpoolApplication a WHERE a.status = 1")
    long countConfirmed();
    
    @Query("SELECT COUNT(DISTINCT a.post) FROM CarpoolApplication a WHERE a.status = 1")
    long countPostsWithConfirmedApplications();
}
