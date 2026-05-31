package com.rinchik.esport.service;

import com.rinchik.esport.dto.methodology.MethodologyEditRequest;
import com.rinchik.esport.dto.methodologyblock.BlockEditRequest;
import com.rinchik.esport.dto.methodologyblock.HeaderBlockEditRequest;
import com.rinchik.esport.dto.methodologyblock.ImageBlockEditRequest;
import com.rinchik.esport.exception.*;
import com.rinchik.esport.model.Methodology;
import com.rinchik.esport.model.Team;
import com.rinchik.esport.model.User;
import com.rinchik.esport.model.methodologyblock.HeaderBlock;
import com.rinchik.esport.model.methodologyblock.ImageBlock;
import com.rinchik.esport.model.methodologyblock.MethodologyBlock;
import com.rinchik.esport.model.methodologyblock.TextBlock;
import com.rinchik.esport.repository.MethodologyBlockRepository;
import com.rinchik.esport.repository.MethodologyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MethodologyService {
    private final MethodologyRepository methRepo;
    private final MethodologyBlockRepository blockRepo;
    private final UserService userService;
    private final TeamService teamService;

    public List<Methodology> findMethodologiesByTeam(Long teamId) {
        Team team = teamService.findTeamById(teamId);
        return methRepo.findByTeam(team);
    }

    public List<Methodology> findAllMethodologies() {
        return methRepo.findAll();
    }

    public Methodology findMethodologyById(Long id) {
        return methRepo.findById(id)
                .orElseThrow(() -> new MethodologyNotFoundException(id));
    }

    public Methodology findMethodologyByIdIfAvailable(Long id, Long userId) {
        Methodology meth = findMethodologyById(id);
        User user = userService.findUserById(userId);
        if (!meth.getTeam().getId().equals(user.getTeam().getId()))
            throw new UserNotTeamMemberException(userId, meth.getTeam().getId());
        return meth;
    }

    @Transactional
    public Methodology createNewMethodology(Long authorId, MethodologyEditRequest dto) {
        Methodology newMethodology = new Methodology();

        newMethodology.setTitle(dto.getInfo().getTitle());
        newMethodology.setDescription(dto.getInfo().getDescription() == null ? null : dto.getInfo().getDescription());
        newMethodology.setLevel(dto.getInfo().getLevel());
        newMethodology.setImageUrl(dto.getInfo().getImageUrl() == null ? null : dto.getInfo().getImageUrl());
        newMethodology.setCategory(dto.getInfo().getCategory() == null ? null : dto.getInfo().getCategory());
        newMethodology.setDuration(dto.getInfo().getDuration() == null ? null : dto.getInfo().getDuration());

        User author = userService.findUserById(authorId);
        newMethodology.setTeam(author.getTeam());
        newMethodology.setAuthor(author);

        for (BlockEditRequest b : dto.getContent()) {
            MethodologyBlock block;
            if (b instanceof HeaderBlockEditRequest) {
                block = new HeaderBlock();
                ((HeaderBlock) block).setHeader(b.getContent());
            } else if (b instanceof ImageBlockEditRequest) {
                block = new ImageBlock();
                ((ImageBlock) block).setImageUrl(b.getContent());
            } else {
                block = new TextBlock();
                ((TextBlock) block).setText(b.getContent());
            }
            block.setOrderIndex(b.getOrderIndex());
            block.setMethodology(newMethodology);
            newMethodology.getContent().add(block);
            blockRepo.save(block);
        }

        return methRepo.save(newMethodology);
    }

    @Transactional
    public void deleteMethodology(Long id) {
        Methodology methodology = findMethodologyById(id);
        methodology.setAuthor(null);
        methRepo.delete(methodology);
    }

    @Transactional
    public void deleteMethodologyByCaptain(Long methId, Long captainId) {
        User author = userService.findUserById(captainId);
        Methodology meth = findMethodologyById(methId);
        if (!meth.getTeam().getId().equals(author.getTeam().getId()))
            throw new UserNotTeamMemberException(captainId, meth.getTeam().getId());
        deleteMethodology(methId);
    }

    public MethodologyBlock findMethodologyBlockById(Long id) {
        return blockRepo.findById(id)
                .orElseThrow(() -> new MethodologyBlockNotFoundException(id));
    }

    @Transactional
    public Methodology updateMethodologyByAuthor(MethodologyEditRequest dto, Long methId, Long authorId) {
        Methodology meth = findMethodologyById(methId);
        if (!meth.getAuthor().getId().equals(authorId))
            throw new UserNotMethodologyAuthorException(authorId, methId);
        meth.setTitle(dto.getInfo().getTitle());
        if (dto.getInfo().getDescription() != null)
            meth.setDescription(dto.getInfo().getDescription());
        if (dto.getInfo().getImageUrl() != null)
            meth.setImageUrl(dto.getInfo().getImageUrl());
        if (dto.getInfo().getDuration() != null)
            meth.setDuration(dto.getInfo().getDuration());
        if (dto.getInfo().getCategory() != null)
            meth.setCategory(dto.getInfo().getCategory());
        meth.setLevel(dto.getInfo().getLevel());

        Map<Long, Boolean> blockUsages = new HashMap<>();
        for (MethodologyBlock b : meth.getContent())
            blockUsages.put(b.getId(), false);

        for (var dtoBlock : dto.getContent()) {
            MethodologyBlock block;
            if (dtoBlock.getId() != null) {
                block = findMethodologyBlockById(dtoBlock.getId());
                blockUsages.replace(block.getId(), true);
            }
            else {
                if (dtoBlock instanceof HeaderBlockEditRequest)
                    block = new HeaderBlock();
                else if (dtoBlock instanceof ImageBlockEditRequest)
                    block = new ImageBlock();
                else
                    block = new TextBlock();
                block.setMethodology(meth);
                meth.getContent().add(block);
                blockRepo.save(block);
            }
            switch (block.getBlockType()) {
                case HEADER -> ((HeaderBlock) block).setHeader(dtoBlock.getContent());
                case IMAGE -> ((ImageBlock) block).setImageUrl(dtoBlock.getContent());
                case TEXT -> ((TextBlock) block).setText(dtoBlock.getContent());
            }
            block.setOrderIndex(dtoBlock.getOrderIndex());
        }

        for (Map.Entry<Long, Boolean> use : blockUsages.entrySet())
            if (!use.getValue()) {
                MethodologyBlock block = findMethodologyBlockById(use.getKey());
                meth.getContent().remove(block);
                blockRepo.delete(block);
            }

        return meth;
    }
}