package com.rinchik.esport.model.methodologyblock;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
@DiscriminatorValue("IMAGE")
public class ImageBlock extends MethodologyBlock {
    private String imageUrl;
}
