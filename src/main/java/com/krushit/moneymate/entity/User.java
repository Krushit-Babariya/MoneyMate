package com.krushit.moneymate.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "full_name", length = 30, nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "activation_token")
    private String activationToken;

    @PrePersist
    public void prePersist() {
        if (this.isActive == null) {
            isActive = false;
        }
    }
}