package com.rinchik.esport.dto.methodologyblock;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.rinchik.esport.model.enums.MethodologyBlockType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextBlockInfoResponse.class, name = "TEXT"),
        @JsonSubTypes.Type(value = ImageBlockInfoResponse.class, name = "IMAGE"),
        @JsonSubTypes.Type(value = HeaderBlockInfoResponse.class, name = "HEADER")
})
public abstract class BlockEditRequest {
    private Long id;

    @NotNull
    private int orderIndex;

    public abstract String getContent();
}
