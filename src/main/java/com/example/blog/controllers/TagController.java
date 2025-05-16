package com.example.blog.controllers;


import com.example.blog.domain.dtos.CreateTagsRequest;
import com.example.blog.domain.dtos.TagDto;
import com.example.blog.domain.entities.Tag;
import com.example.blog.mappers.TagMapper;
import com.example.blog.services.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
public class TagController {
    private final TagService tagService;
    private final TagMapper tagMapper;
    @GetMapping
    public ResponseEntity<List<TagDto>> getAllTags(){
        List<Tag> tags=tagService.getTags();
        List<TagDto> tagResponses =tags.stream().map(tagMapper::toTagResponse).toList();
        return ResponseEntity.ok(tagResponses);
    }

    @PostMapping
    public ResponseEntity<List<TagDto>> createTags(@Valid @RequestBody CreateTagsRequest createTagsRequest){
        List<Tag> savedTags=tagService.createTags(createTagsRequest.getNames());
        List<TagDto> tagResponses =savedTags.stream().map(tagMapper::toTagResponse).toList();
        return new ResponseEntity<>(tagResponses, HttpStatus.CREATED);
    }

    @DeleteMapping(path="/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable UUID id){
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }

}
