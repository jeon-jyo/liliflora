package com.liliflora.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
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
    @Column(nullable = false, name = "reg_date")
    private LocalDate regDate;

    @LastModifiedDate
    @Column(name = "changed_date")
    private LocalDate changedDate;

    @Column(nullable = false)
    private char status;


    // Entity 가 DB에 Insert 되기 전에 호출됨
    @PrePersist
    public void prePersist() {
        this.regDate = LocalDate.now();
        this.changedDate = LocalDate.now();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
