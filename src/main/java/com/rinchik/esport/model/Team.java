package com.rinchik.esport.model;

import com.rinchik.esport.model.enums.Game;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
@NoArgsConstructor
@Data
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    private String contacts;
    private String requirements;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Game game;

    @OneToMany(mappedBy = "team")
    private List<User> members = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.REMOVE)
    private List<Event> events = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "captain_id", nullable = false, unique = true)
    private User captain;

    @OneToMany(mappedBy = "team", cascade = CascadeType.REMOVE)
    private List<Methodology> methodologies = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.REMOVE)
    private List<TeamRequest> requests = new ArrayList<>();
}
