package com.costi.csw9.Repository;

import com.costi.csw9.Model.Light;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LightRepository {
    List<Light> findAllByOrderByDateAddedDesc();
    List<Light> findAllByIsEnabledTrueAndIsPublicTrueOrderByDateAddedDesc();

    List<Light> findAll();

    void save(Light light);

    Optional<Light> findById(Long id);

    void deleteById(Long id);
}
