package com.costi.csw9.Repository;

import com.costi.csw9.Model.User;
import com.costi.csw9.Model.WikiPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@EnableJpaRepositories
public interface WikiRepository extends JpaRepository<WikiPage, Long> {
    List<WikiPage> findByAuthor_Id(Long id);
    List<WikiPage> findByCategory(String category);
    List<WikiPage> findByEnabled(boolean enabled);
    List<WikiPage> findByAuthor(User user);
}
