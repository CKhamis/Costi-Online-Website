package com.costi.csw9.Repository;

import com.costi.csw9.Model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentDao extends JpaRepository<Attachment, String> {
}
