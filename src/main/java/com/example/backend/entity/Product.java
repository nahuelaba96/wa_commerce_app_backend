package com.example.backend.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity 
@Table (name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String brand;

    @ManyToOne 
    @JoinColumn (name = "merchant_id")
    private Merchant merchant;

    @ManyToOne
    @JoinColumn (name = "category_id")
    private Category category;

    @OneToMany (mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductVariant> productVariants = new ArrayList<>();

    @OneToMany (mappedBy = "product")
    private List<ProductImage> images = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

}
