package com.rinchik.esport.model;

import com.rinchik.esport.model.enums.TeamRequestStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "team_requests")
@NoArgsConstructor
@Data
public class TeamRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    private String message;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TeamRequestStatus status;
}
