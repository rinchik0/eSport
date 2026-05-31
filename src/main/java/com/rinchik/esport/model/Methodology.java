package com.rinchik.esport.model;

import com.rinchik.esport.model.enums.MethodologyLevel;
import com.rinchik.esport.model.methodologyblock.MethodologyBlock;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "methodologies")
@NoArgsConstructor
@Data
public class Methodology {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;
    private String duration;
    private String category;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MethodologyLevel level;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "methodology", cascade = CascadeType.ALL)
    @OrderBy("orderIndex ASC")
    private List<MethodologyBlock> content = new ArrayList<>();
}
