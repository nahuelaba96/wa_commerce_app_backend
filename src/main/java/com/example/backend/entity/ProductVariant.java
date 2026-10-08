package com.example.backend.entity;

import java.time.LocalDateTime;
import java.math.BigDecimal;

import org.hibernate.annotations.CreationTimestamp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity 
@Table (name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
public class ProductVariant {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sku;

    private String size;

    private String color;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @ManyToOne 
    @JoinColumn (name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer stock = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
