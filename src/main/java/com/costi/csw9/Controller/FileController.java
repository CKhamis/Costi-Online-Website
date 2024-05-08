package com.costi.csw9.Controller;

import com.costi.csw9.Model.Attachment;
import com.costi.csw9.Model.DTO.FileUploadResponseData;
import com.costi.csw9.Service.AttachmentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static com.costi.csw9.Util.LogicTools.POST_IMAGE_PATH;

@RestController
public class FileController {

    private AttachmentService attachmentService;

    public FileController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/Upload")
    public FileUploadResponseData uploadFile(@RequestParam("file")MultipartFile file) throws Exception {
        Attachment attachment = attachmentService.saveAttachment(file, false);
        String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(POST_IMAGE_PATH)
                .path(attachment.getId())
                .toUriString();
        return new FileUploadResponseData(attachment.getFilename(), downloadURL, file.getContentType(), file.getSize());
    }

    @GetMapping(POST_IMAGE_PATH + "{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) throws Exception {
        Attachment attachment = attachmentService.getAttachment(fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= \"" + attachment.getFilename() + "\"")
                .body(new ByteArrayResource(attachment.getData()));
    }
}
