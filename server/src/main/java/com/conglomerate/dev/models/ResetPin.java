package com.conglomerate.dev.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reset_pins")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String hashedPin;

    @OneToOne
    @JoinColumn(name = "user_id")
    User user;

    LocalDateTime expiration;
}
