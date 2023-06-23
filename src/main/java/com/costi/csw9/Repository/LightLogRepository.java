package com.costi.csw9.Repository;

import com.costi.csw9.Model.Light;
import com.costi.csw9.Model.LightLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LightLogRepository extends JpaRepository<LightLog, Long> {
    List<LightLog> findAllByLightOrderByDateCreatedDesc(Light light);
}