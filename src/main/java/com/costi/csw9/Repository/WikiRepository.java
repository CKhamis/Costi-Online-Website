package com.costi.csw9.Repository;

import com.costi.csw9.Model.User;
import com.costi.csw9.Model.WikiCategory;
import com.costi.csw9.Model.WikiPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface WikiRepository {
    List<WikiPage> findByAuthor_Id(Long id);
    List<WikiPage> findByCategory(String category);
    List<WikiPage> findByEnabled(boolean enabled);
    List<WikiPage> findByAuthor(User user);

    WikiPage save(WikiPage page);

    List<WikiPage> findAll();

    void deleteById(Long id);

    Optional<WikiPage> findById(Long id);
}
