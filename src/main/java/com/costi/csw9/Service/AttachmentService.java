package com.costi.csw9.Service;

import com.costi.csw9.Model.Attachment;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    Attachment saveAttachment(MultipartFile file, boolean isLocked) throws Exception;

    Attachment getAttachment(String fileId) throws Exception;
}
