package com.costi.csw9.Repository;

import com.costi.csw9.Model.User;
import com.costi.csw9.Model.WikiCategory;
import com.costi.csw9.Model.WikiPage;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface WikiRepository {
    WikiPage findById(Long id);
    List<WikiPage> findByAuthor(Long id);
    List<WikiPage> findByCategory(String category);
    List<WikiPage> getByApproval(boolean enabled);
    List<WikiPage> findAll();
    @Modifying
    void save(WikiPage wikiPage);
    @Modifying
    void delete(Long id);
    @Modifying
    void enable(Long id, boolean enable);
}
