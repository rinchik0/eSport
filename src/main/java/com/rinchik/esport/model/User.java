package com.rinchik.esport.model;

import com.rinchik.esport.model.enums.SystemRole;
import com.rinchik.esport.model.enums.TeamRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<SystemRole> roles = new HashSet<>();

    private String bio;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime lastOnline;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime joinDate;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private TeamRole roleInTeam;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "organizer", cascade = CascadeType.REMOVE)
    private List<Event> organizedEvents = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    private List<Methodology> methodologies = new ArrayList<>();

    @OneToOne(mappedBy = "captain", cascade = CascadeType.REMOVE)
    private Team captainedTeam;

    @ManyToMany(mappedBy = "participants")
    private List<Event> eventsParticipating = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Rates rates;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<TeamRequest> requests = new ArrayList<>();

    private String faceitPlayerId;
    private String faceitNickname;
    private String steamId;
}
