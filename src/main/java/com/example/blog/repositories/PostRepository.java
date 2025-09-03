package com.example.blog.repositories;

import com.example.blog.domain.PostStatus;
import com.example.blog.domain.entities.Category;
import com.example.blog.domain.entities.Post;
import com.example.blog.domain.entities.Tag;
import com.example.blog.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findAllByStatusAndCategoryAndTagsContaining(PostStatus postStatus, Category category, Tag tag);

    List<Post> findAllByStatusAndTagsContaining(PostStatus postStatus, Tag tag);

    List<Post> findAllByStatusAndCategory(PostStatus postStatus, Category category);

    List<Post> findAllByStatus(PostStatus postStatus);

    List<Post> findAllByAuthorAndStatus(User author, PostStatus postStatus);
}
