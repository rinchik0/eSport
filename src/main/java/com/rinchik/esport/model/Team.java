package com.rinchik.esport.model;

import com.rinchik.esport.model.enums.Game;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Game game;

    @OneToMany(mappedBy = "team")
    private List<User> members = new ArrayList<>();

    @OneToMany(mappedBy = "team")
    private List<Event> events = new ArrayList<>();
}
