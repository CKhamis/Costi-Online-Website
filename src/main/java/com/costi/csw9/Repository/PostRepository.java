package com.costi.csw9.Repository;

import com.costi.csw9.Model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface PostRepository {
    Post findById(Long id);
    List<Post> findByCategory(String category);
    List<Post> getByApproval(boolean enabled);
    @Modifying
    void save(Post page);
    @Modifying
    void delete(Long id);
    @Modifying
    void enable(Long id, boolean enable);
}
