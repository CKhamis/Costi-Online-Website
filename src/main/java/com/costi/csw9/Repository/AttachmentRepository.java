package com.costi.csw9.Repository;

import com.costi.csw9.Model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttachmentRepository{
    List<Attachment> findAllByOrderByCreatedDesc();

    void deleteById(String substring);

    Optional<Attachment> findById(String id);

    Attachment save(Attachment attachment);
}
