package com.rinchik.esport.dto.methodologyblock;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextBlockEditRequest.class, name = "TEXT"),
        @JsonSubTypes.Type(value = ImageBlockEditRequest.class, name = "IMAGE"),
        @JsonSubTypes.Type(value = HeaderBlockEditRequest.class, name = "HEADER")
})
public abstract class BlockEditRequest {
    private Long id;

    @NotNull
    @JsonProperty("order_index")
    private int orderIndex;

    public abstract String getContent();
}
