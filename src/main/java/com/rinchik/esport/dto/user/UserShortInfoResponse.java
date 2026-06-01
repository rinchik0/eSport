package com.rinchik.esport.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserShortInfoResponse {
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("user_name")
    private String userName;
}
