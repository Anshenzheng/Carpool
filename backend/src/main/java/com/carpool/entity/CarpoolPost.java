package com.carpool.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "carpool_posts")
public class CarpoolPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String departure;

    @Column(nullable = false)
    private String destination;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private Integer seats;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Column(name = "contact_name", nullable = false)
    private String contactName;

    @Column(name = "contact_phone", nullable = false)
    private String contactPhone;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 0-待审核, 1-已发布, 2-已下架, 3-已取消
     */
    private Integer status = 0;

    /**
     * 0-待审核, 1-审核通过, 2-审核拒绝
     */
    @Column(name = "audit_status")
    private Integer auditStatus = 0;

    @Column(name = "audit_remark")
    private String auditRemark;

    @ManyToOne
    @JoinColumn(name = "auditor_id")
    private User auditor;

    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
