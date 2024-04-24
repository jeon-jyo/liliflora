package com.liliflora.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Builder
@Getter
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @CreatedDate
    @Column(name = "reg_date", nullable = false, updatable = false)
    private LocalDateTime regDate;

    @LastModifiedDate
    @Column(name = "changed_date")
    private LocalDateTime changedDate;

    @Column(nullable = false)
    private char status;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Wishlist wishlist;


    // Entity 가 DB에 Insert 되기 전에 호출됨
    @PrePersist
    public void prePersist() {
        this.regDate = LocalDateTime.now();
        this.changedDate = LocalDateTime.now();
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updatePhone(String phone) {
        this.phone = phone;
    }

    public void updateAddress(String address) {
        this.address = address;
    }
}
