package com.rinchik.esport.dto.faceit;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FaceitConnectRequest {
    @NotBlank(message = "Nickname can not be empty")
    @JsonProperty("faceit_nickname")
    private String faceitNickname;
}
