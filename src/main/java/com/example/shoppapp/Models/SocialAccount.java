package com.example.shoppapp.Models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "social_accounts")
public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider",nullable = false,length = 20)
    private String provider;

    @Column(name = "provider_id",nullable = false,length = 20)
    private String providerID;

    @Column(name = "name",length = 150)
    private String name;

    @Column(name = "email",length = 150)
    private String email;

}
