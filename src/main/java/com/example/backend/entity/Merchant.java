package com.example.backend.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table (name = "merchants")
@NoArgsConstructor 
@Getter 
@Setter
public class Merchant {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany (mappedBy = "merchant")
    private List<Product> products = new ArrayList<>();

    @OneToMany (mappedBy = "merchant")
    private List<Category> categories = new ArrayList<>();

    @OneToMany (mappedBy = "merchant")
    private List<MerchantUser> merchantUsers = new ArrayList<>();

    @OneToMany (mappedBy = "merchant")
    private List<Customer> customers = new ArrayList<>();

    @OneToMany (mappedBy = "merchant")
    private List<Conversation> conversations = new ArrayList<>();

    @OneToMany (mappedBy = "merchant")
    private List<WhatsAppAccount> whatsappAccounts = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;
}
