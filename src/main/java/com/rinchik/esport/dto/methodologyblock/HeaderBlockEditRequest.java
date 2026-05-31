package com.rinchik.esport.dto.methodologyblock;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class HeaderBlockEditRequest extends BlockEditRequest {
    @NotNull(message = "Header block can not be empty")
    private String content;

    @Override
    public String getContent() {
        return content;
    }
}
