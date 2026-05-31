package com.rinchik.esport.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "rates")
@NoArgsConstructor
@Data
public class Rates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private Double KD;
    private Double ADR;
    private Double WinRate;
    private Double trainingAttendance;
    private Double tournamentPlayed;
    private double hoursPlayed;

    @UpdateTimestamp
    private LocalDateTime lastUpdateDate;
}
