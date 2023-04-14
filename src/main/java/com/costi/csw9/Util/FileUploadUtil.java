package com.costi.csw9.Util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {
  private static final String UPLOAD_DIRECTORY = "/home/costi/Costi-Online-Website/src/main/resources/static/uploads/posts/";

  public static void saveFile(String subDirectory, String fileName, MultipartFile multipartFile) throws IOException {
    Path uploadPath = Paths.get(UPLOAD_DIRECTORY + subDirectory);
    // TODO: check for ../ xxs vulnerability
    if (!Files.exists(uploadPath)) {
      Files.createDirectories(uploadPath);
    }

    try (InputStream inputStream = multipartFile.getInputStream()) {
      Path filePath = uploadPath.resolve(fileName);
      System.out.println(filePath.toFile().getAbsolutePath());
      Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException ioe) {
      throw new IOException("Could not save image file: " + fileName, ioe);
    }
  }
}