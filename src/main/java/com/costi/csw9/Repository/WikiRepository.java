package com.costi.csw9.Repository;

import com.costi.csw9.Model.User;
import com.costi.csw9.Model.WikiCategory;
import com.costi.csw9.Model.WikiPage;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WikiRepository {
    WikiPage findById(Long id);
    List<WikiPage> findByCategory(WikiCategory category);
    List<WikiPage> findAll();
    void save(WikiPage wikiPage);
    void delete(WikiPage wikiPage);
}
