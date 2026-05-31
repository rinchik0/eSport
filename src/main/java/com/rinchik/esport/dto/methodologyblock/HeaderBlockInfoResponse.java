package com.rinchik.esport.dto.methodologyblock;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HeaderBlockInfoResponse extends BlockInfoResponse {
    private String content;

    @Override
    public void setContent(String content) {
        this.content = content;
    }
}
