package com.costi.csw9.Service;

import com.costi.csw9.Model.Attachment;
import com.costi.csw9.Repository.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AttachmentServiceImpl implements AttachmentService {
    private AttachmentRepository attachmentRepository;

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }


    @Override
    public Attachment saveAttachment(MultipartFile file) throws Exception {
       String fileName = StringUtils.cleanPath(file.getOriginalFilename());
       try {
            if(fileName.contains("..")) {
                throw  new Exception("Filename contains invalid path sequence: " + fileName);
            }else if(file.isEmpty()){
                throw  new Exception("Filename is empty:  " + fileName);
            }

            Attachment attachment = new Attachment(fileName, file.getContentType(), file.getBytes());
            return attachmentRepository.save(attachment);

       } catch (Exception e) {
            throw new Exception("File was not saved: " + fileName);
       }
    }

    @Override
    public Attachment getAttachment(String fileId) throws Exception {
        return attachmentRepository
                .findById(fileId)
                .orElseThrow(
                        () -> new Exception("File not found with Id: " + fileId));
    }
}
