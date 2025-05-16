package com.example.blog.controllers;

import com.example.blog.domain.CreatePostRequest;
import com.example.blog.domain.UpdatePostRequest;
import com.example.blog.domain.dtos.CreatePostRequestDto;
import com.example.blog.domain.dtos.PostDto;
import com.example.blog.domain.dtos.UpdatePostRequestDto;
import com.example.blog.domain.entities.Post;
import com.example.blog.domain.entities.User;
import com.example.blog.mappers.PostMapper;
import com.example.blog.services.PostService;
import com.example.blog.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false)UUID tagId) {
            List<Post> posts= postService.getAllPosts(categoryId, tagId);
            List<PostDto> postDtos=posts.stream().map(postMapper::toDto).toList();
            return ResponseEntity.ok(postDtos);
    }

    @GetMapping(path="/drafts")
    public ResponseEntity<List<PostDto>> getAllDrafts(@RequestAttribute UUID userId) {
        User loggedInUser = userService.getUserById(userId);
        List<Post> draftPosts=postService.getGraftPosts(loggedInUser);
        List<PostDto> postDtos=draftPosts.stream().map(postMapper::toDto).toList();
        return ResponseEntity.ok(postDtos);
    }
    @PostMapping
    public ResponseEntity<PostDto> createPost(
            @Valid @RequestBody CreatePostRequestDto createPostRequestDto,
            @RequestAttribute UUID userId) {
        User loggedInUser = userService.getUserById(userId);
        CreatePostRequest createPostRequest= postMapper.toCreatePostRequest(createPostRequestDto);
        Post createdPost=postService.createPost(loggedInUser, createPostRequest);
        PostDto createdPostDto= postMapper.toDto(createdPost);
        return new ResponseEntity<>(createdPostDto, HttpStatus.CREATED);
    }
    @PutMapping(path="/{id}")
    public ResponseEntity<PostDto> updatePost(@PathVariable UUID id,
                                              @Valid @RequestBody UpdatePostRequestDto updatePostRequestDto){
        UpdatePostRequest updatePostRequest=postMapper.toUpdatePostRequest(updatePostRequestDto);
        Post updatedPost=postService.updatePost(id,updatePostRequest);
        PostDto postDto=postMapper.toDto(updatedPost);
        return ResponseEntity.ok(postDto);
    }
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable UUID id){
        Post post=postService.getPost(id);
        PostDto postDto=postMapper.toDto(post);
        return new ResponseEntity<>(postDto, HttpStatus.OK);
    }
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<HttpStatus> deletePost(@PathVariable UUID id){
        postService.deletePost(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
