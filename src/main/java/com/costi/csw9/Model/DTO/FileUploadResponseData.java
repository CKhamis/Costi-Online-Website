package com.costi.csw9.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponseData {

    private String fileName;
    private String downloadURL;
    private String fileType;
    private long fileSize;
}
