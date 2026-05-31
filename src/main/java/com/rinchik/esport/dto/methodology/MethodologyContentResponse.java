package com.rinchik.esport.dto.methodology;

import com.rinchik.esport.dto.methodologyblock.BlockInfoResponse;
import lombok.Data;

import java.util.ArrayList;

@Data
public class MethodologyContentResponse {
    private MethodologyInfoResponse info;
    private ArrayList<BlockInfoResponse> content;
}
