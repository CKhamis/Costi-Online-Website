package com.costi.csw9.Repository;

import com.costi.csw9.Model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByCategoryOrderByLastEditedDesc(String category);
    List<Post> findByCategoryAndIdNotOrderByLastEditedDesc(String category, Long exception);
    List<Post> findByEnabledOrderByLastEditedDesc(boolean enabled);
    List<Post> findByEnabledAndIdNotOrderByLastEditedDesc(boolean enabled, Long exception);
    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.enabled = :enabled WHERE p.id = :id")
    void setEnabledById(@Param("id") Long id, @Param("enabled") boolean enabled);
}
