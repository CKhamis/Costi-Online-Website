package com.costi.csw9.Repository;

import com.costi.csw9.Model.Post;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PostRepository {
    Post findById(Long id);
    List<Post> findByCategory(String category);
    List<Post> findByCategory(String category, Long exception);
    List<Post> getByApproval(boolean enabled);

    List<Post> getByApproval(boolean enabled, Long exception);

    @Modifying
    void save(Post page);
    @Modifying
    void delete(Long id);
    @Modifying
    void enable(Long id, boolean enable);
}