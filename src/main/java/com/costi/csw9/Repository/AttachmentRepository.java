package com.costi.csw9.Repository;

import com.costi.csw9.Model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, String> {
    public List<Attachment> findAllByOrderByCreatedDesc();
}
