package com.rinchik.esport.model.methodologyblock;

import com.rinchik.esport.model.Methodology;
import com.rinchik.esport.model.enums.MethodologyBlockType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "methodology_blocks")
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@Data
public class MethodologyBlock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int orderIndex;

    @ManyToOne
    @JoinColumn(name = "methodology_id")
    private Methodology methodology;

    @Transient
    public MethodologyBlockType getBlockType() {
        String value = this.getClass().getAnnotation(DiscriminatorValue.class).value();
        return MethodologyBlockType.valueOf(value);
    }
}
