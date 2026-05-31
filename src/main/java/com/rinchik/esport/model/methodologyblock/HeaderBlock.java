package com.rinchik.esport.model.methodologyblock;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
@DiscriminatorValue("HEADER")
public class HeaderBlock extends MethodologyBlock {
    private String header;
}
