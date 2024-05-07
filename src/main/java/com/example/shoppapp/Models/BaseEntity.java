package com.example.shoppapp.Models;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class BaseEntity {
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "create_at")
    private LocalDateTime createAt;
    @PrePersist
    protected void onCreate(){
        createAt= LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        updateAt=LocalDateTime.now();
    }
}
