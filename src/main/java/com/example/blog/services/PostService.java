package com.example.blog.services;

import com.example.blog.domain.CreatePostRequest;
import com.example.blog.domain.UpdatePostRequest;
import com.example.blog.domain.entities.Post;
import com.example.blog.domain.entities.User;

import java.util.List;
import java.util.UUID;

public interface PostService {
    Post getPost(UUID id);
    List<Post> getAllPosts(UUID categoryId,UUID tagId);
    List<Post> getGraftPosts(User user);
    Post createPost(User user, CreatePostRequest postRequest);
    Post updatePost(UUID id,UpdatePostRequest postRequest);
    void deletePost(UUID id);
}
