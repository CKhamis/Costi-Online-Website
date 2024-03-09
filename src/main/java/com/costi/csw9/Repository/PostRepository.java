package com.costi.csw9.Repository;

import com.costi.csw9.Model.Post;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository {
    List<Post> findByEnabledAndIsPublicOrderByLastEditedDesc(boolean enabled, boolean isPublic);

    Optional<Post> findById(Long id);

    List<Post> findAll();

    void deleteById(Long id);

    void save(Post post);

    Post getById(Long id);
}
