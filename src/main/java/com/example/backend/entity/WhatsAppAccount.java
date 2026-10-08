package com.example.backend.entity;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "whatsapp_accounts")
@Getter
@Setter
@NoArgsConstructor
public class WhatsAppAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    @OneToMany(mappedBy = "whatsappAccount")
    private List<Conversation> conversations = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;
}
