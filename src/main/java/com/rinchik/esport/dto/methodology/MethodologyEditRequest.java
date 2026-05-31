package com.rinchik.esport.dto.methodology;

import com.rinchik.esport.dto.methodologyblock.BlockEditRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;

@Data
public class MethodologyEditRequest {
    @NotNull
    private MethodologyInfoEditRequest info;

    @NotEmpty(message = "Content should not be empty")
    private ArrayList<BlockEditRequest> content;
}
