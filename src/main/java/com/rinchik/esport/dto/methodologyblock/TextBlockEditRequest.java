package com.rinchik.esport.dto.methodologyblock;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TextBlockEditRequest extends BlockEditRequest {
    @NotNull(message = "Text block can not be empty")
    private String content;

    @Override
    public String getContent() {
        return content;
    }
}
