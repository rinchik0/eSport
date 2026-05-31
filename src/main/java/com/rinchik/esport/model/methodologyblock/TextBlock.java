package com.rinchik.esport.model.methodologyblock;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
@DiscriminatorValue("TEXT")
public class TextBlock extends MethodologyBlock{
    @Column(columnDefinition = "TEXT")
    private String text;
}
