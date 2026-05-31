package com.rinchik.esport.mapper;

import com.rinchik.esport.dto.methodology.MethodologyContentResponse;
import com.rinchik.esport.dto.methodology.MethodologyInfoResponse;
import com.rinchik.esport.dto.methodologyblock.BlockInfoResponse;
import com.rinchik.esport.dto.methodologyblock.HeaderBlockInfoResponse;
import com.rinchik.esport.dto.methodologyblock.ImageBlockInfoResponse;
import com.rinchik.esport.dto.methodologyblock.TextBlockInfoResponse;
import com.rinchik.esport.model.Methodology;
import com.rinchik.esport.model.methodologyblock.HeaderBlock;
import com.rinchik.esport.model.methodologyblock.ImageBlock;
import com.rinchik.esport.model.methodologyblock.MethodologyBlock;
import com.rinchik.esport.model.methodologyblock.TextBlock;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MethodologyMapper {
    public MethodologyInfoResponse toMethodologyInfoResponse(Methodology methodology) {
        MethodologyInfoResponse dto = new MethodologyInfoResponse();
        dto.setId(methodology.getId());
        dto.setTitle(methodology.getTitle());
        dto.setDescription(methodology.getDescription());
        dto.setAuthorId(methodology.getAuthor().getId());
        dto.setAuthorName(methodology.getAuthor().getUsername());
        dto.setImageUrl(methodology.getImageUrl());
        dto.setDuration(methodology.getDuration());
        dto.setCategory(methodology.getCategory());
        dto.setLevel(methodology.getLevel());
        dto.setTeamId(methodology.getTeam().getId());
        return dto;
    }

    public BlockInfoResponse toBlockInfoResponse(MethodologyBlock block) {
        switch(block.getBlockType()) {
            case TEXT -> {
                BlockInfoResponse dto = new TextBlockInfoResponse();
                dto.setContent(((TextBlock)block).getText());
                dto.setOrderIndex(block.getOrderIndex());
                dto.setId(block.getId());
                return dto;
            }
            case IMAGE -> {
                BlockInfoResponse dto = new ImageBlockInfoResponse();
                dto.setContent(((ImageBlock)block).getImageUrl());
                dto.setOrderIndex(block.getOrderIndex());
                dto.setId(block.getId());
                return dto;
            }
            case HEADER -> {
                BlockInfoResponse dto = new HeaderBlockInfoResponse();
                dto.setContent(((HeaderBlock)block).getHeader());
                dto.setOrderIndex(block.getOrderIndex());
                dto.setId(block.getId());
                return dto;
            }
            default -> throw new IllegalArgumentException("Unknown block type");
        }
    }

    public MethodologyContentResponse toMethodologyContentResponse(Methodology methodology) {
        MethodologyContentResponse dto = new MethodologyContentResponse();
        MethodologyInfoResponse info = toMethodologyInfoResponse(methodology);
        ArrayList<BlockInfoResponse> blocks = new ArrayList<>();
        for (var block : methodology.getContent())
            blocks.add(toBlockInfoResponse(block));
        dto.setInfo(info);
        dto.setContent(blocks);
        return dto;
    }
}
