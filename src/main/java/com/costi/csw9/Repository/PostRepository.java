package com.costi.csw9.Repository;

import com.costi.csw9.Model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Repository;

import java.util.List;
@EnableJpaRepositories
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByEnabledAndIsPublicOrderByLastEditedDesc(boolean enabled, boolean isPublic);
}
