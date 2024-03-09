package com.costi.csw9.Repository;

import com.costi.csw9.Model.DynamicContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Repository;
@EnableJpaRepositories
@Repository
public interface DynamicContentRepository extends JpaRepository<DynamicContent, Long> {
    DynamicContent findTopByEnabledOrderByLastEditedDesc(boolean enabled);

    @Modifying
    @Transactional
    @Query("UPDATE DynamicContent e SET e.enabled = false")
    void disableAll();

    @Modifying
    @Transactional
    @Query("UPDATE DynamicContent e SET e.enabled = true WHERE e.id = :id")
    void enable(Long id);
}
