package com.costi.csw9.Service;

import com.costi.csw9.Model.Attachment;
import com.costi.csw9.Repository.AttachmentDao;
import com.costi.csw9.Service.AttachmentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AttachmentServiceImpl implements AttachmentService {
    private AttachmentDao attachmentDao;

    public AttachmentServiceImpl(AttachmentDao attachmentDao) {
        this.attachmentDao = attachmentDao;
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
            return attachmentDao.save(attachment);

       } catch (Exception e) {
            throw new Exception("File was not saved: " + fileName);
       }
    }

    @Override
    public Attachment getAttachment(String fileId) throws Exception {
        return attachmentDao
                .findById(fileId)
                .orElseThrow(
                        () -> new Exception("File not found with Id: " + fileId));
    }
}
