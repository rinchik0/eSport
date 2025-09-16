package com.rinchik.esport.model;

import com.rinchik.esport.model.enums.SystemRole;
import com.rinchik.esport.model.enums.TeamRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SystemRole role;

    private String name;

    @Enumerated(EnumType.STRING)
    private TeamRole roleInTeam;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "organizer")
    private List<Event> organizedEvents = new ArrayList<>();

    @ManyToMany(mappedBy = "participants")
    private List<Event> eventsParticipating = new ArrayList<>();

    @OneToMany(mappedBy = "author")
    private List<Article> articles = new ArrayList<>();
}
