package com.costi.csw9.Repository;

import com.costi.csw9.Model.DynamicContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DynamicContentRepository extends JpaRepository<DynamicContent, Long> {
    DynamicContent findTopByEnabledOrderByLastEditedDesc(boolean enabled);
}
