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
@Table(name = "conversations")
@Getter
@Setter
@NoArgsConstructor
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "whatsapp_account_id")
    private WhatsAppAccount whatsappAccount;

    @OneToMany(mappedBy = "conversation")
    private List<Message> messages = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;
}
