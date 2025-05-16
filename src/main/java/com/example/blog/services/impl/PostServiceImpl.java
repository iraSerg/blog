package com.example.blog.services.impl;

import com.example.blog.domain.CreatePostRequest;
import com.example.blog.domain.PostStatus;
import com.example.blog.domain.UpdatePostRequest;
import com.example.blog.domain.entities.Category;
import com.example.blog.domain.entities.Post;
import com.example.blog.domain.entities.Tag;
import com.example.blog.domain.entities.User;
import com.example.blog.repositories.PostRepository;
import com.example.blog.services.CategoryService;
import com.example.blog.services.PostService;
import com.example.blog.services.TagService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final TagService tagService;
    private static final int WORDS_PER_MINUTE=200;

    @Override
    public Post getPost(UUID id) {
        return postRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Post does not exists with ID "+ id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getAllPosts(UUID categoryId, UUID tagId) {
        if(categoryId != null && tagId != null) {
            Category category=categoryService.getCategoryById(categoryId);
            Tag tag=tagService.getTagById(tagId);
            return postRepository
                    .findAllByStatusAndCategoryAndTagsContaining(PostStatus.PUBLISHED,category,tag);

        }
        if(categoryId != null) {
            Category category=categoryService.getCategoryById(categoryId);
            return postRepository.findAllByStatusAndCategory(PostStatus.PUBLISHED,category);
        }
        if(tagId != null) {
            Tag tag=tagService.getTagById(tagId);
            return postRepository.findAllByStatusAndTagsContaining(PostStatus.PUBLISHED,tag);
        }
        return postRepository.findAllByStatus(PostStatus.PUBLISHED);
    }

    @Override
    public List<Post> getGraftPosts(User user) {
        return postRepository.findAllByAuthorAndStatus(user, PostStatus.DRAFT);
    }

    @Transactional
    @Override
    public Post createPost(User user, CreatePostRequest postRequest) {
        Post newPost=new Post();
        newPost.setTitle(postRequest.getTitle());
        newPost.setContent(postRequest.getContent());
        newPost.setStatus(postRequest.getStatus());
        newPost.setAuthor(user);
        newPost.setReadingTime(calculateReadingTime(postRequest.getContent()));
        Category category=categoryService.getCategoryById(postRequest.getCategoryId());
        newPost.setCategory(category);
        Set<UUID> tagsIds=postRequest.getTagIds();
        System.out.println(tagsIds);
        List<Tag> tags=tagService.getTagsByIds(tagsIds);
        System.out.println(tags);
        newPost.setTags(new HashSet<>(tags));
        return postRepository.save(newPost);

    }

    @Transactional
    @Override
    public Post updatePost(UUID id, UpdatePostRequest postRequest) {
        Post existingPost=postRepository.findById(id).orElseThrow(
                ()->new EntityNotFoundException("Post does not exist with id "+id));
        existingPost.setTitle(postRequest.getTitle());
        existingPost.setContent(postRequest.getContent());
        existingPost.setStatus(postRequest.getStatus());
        existingPost.setReadingTime(calculateReadingTime(postRequest.getContent()));

        UUID updatePostCategoryId=postRequest.getCategoryId();
        if(!updatePostCategoryId.equals(existingPost.getCategory().getId())) {
            Category newCategory=categoryService.getCategoryById(updatePostCategoryId);
            existingPost.setCategory(newCategory);
        }

        Set<UUID> existingTags=existingPost.getTags().stream().map(Tag::getId).collect(Collectors.toSet());
        Set<UUID> tagIds=postRequest.getTagIds();
        if(!existingTags.equals(tagIds)) {
            List<Tag> newTags=tagService.getTagsByIds(tagIds);
            existingPost.setTags(new HashSet<>(newTags));
        }
        return postRepository.save(existingPost);

    }

    @Override
    public void deletePost(UUID id) {
        Post post =getPost(id);
        postRepository.delete(post);

    }

    private Integer calculateReadingTime(String content) {
        if(content==null||content.isBlank()){
            return 0;
        }
        int wordCount=content.trim().split("\\s+").length;
        return (int) Math.ceil((double) wordCount/WORDS_PER_MINUTE);
    }
}
