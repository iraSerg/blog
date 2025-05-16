package com.example.blog.services.impl;

import com.example.blog.domain.entities.Tag;
import com.example.blog.repositories.TagRepository;
import com.example.blog.services.TagService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    @Override
    public List<Tag> getTags() {
        return tagRepository.findAllWithPostCount();
    }

    @Transactional
    @Override
    public List<Tag> createTags(Set<String> tagNames) {
        List<Tag> existingTags=tagRepository.findByNameIn(tagNames);
        Set<String> existingTagsNames=existingTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
        List<Tag> newTags=tagNames.stream()
                .filter(name->!existingTagsNames.contains(name))
                .map(name->Tag.builder().name(name).build())
                .toList();
        List<Tag> savedTags=new ArrayList<>();
        if(!newTags.isEmpty()) {
            savedTags=tagRepository.saveAll(newTags);
        }
        savedTags.addAll(existingTags);
        return savedTags;
    }

    @Override
    @Transactional
    public void deleteTag(UUID id) {
        tagRepository.findById(id).ifPresent(tag->{
            if(!tag.getPosts().isEmpty()){
                throw new IllegalStateException("Cannot delete tag with posts");
            }
            tagRepository.deleteById(id);
        });
    }

    @Override
    public Tag getTagById(UUID id) {
        return tagRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Tag not found with id: "+id));

    }

    @Override
    public List<Tag> getTagsByIds(Set<UUID> ids) {
        List<Tag> foundTags=  tagRepository.findAllById(ids);
        if(foundTags.size()!=ids.size()){
            throw new EntityNotFoundException("Not all specified tags IDs exists");
        }
        return foundTags;
    }
}
