package com.costi.csw9.Repository;

import com.costi.csw9.Model.Light;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface LightRepository extends JpaRepository<Light, Long> {
    List<Light> findAllByOrderByDateAddedDesc();
    List<Light> findAllByIsEnabledTrueAndIsPublicTrueOrderByDateAddedDesc();
}
