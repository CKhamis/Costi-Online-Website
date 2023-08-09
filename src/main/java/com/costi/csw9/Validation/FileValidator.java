package com.costi.csw9.Validation;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class FileValidator {
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9-_\\.]+$");

    public static boolean isValidFileName(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        // Check if the file name matches the allowed pattern
        if (!FILE_NAME_PATTERN.matcher(fileName).matches()) {
            return false;
        }

        // Check for security threats in the file name
        if (fileName.contains("../") || fileName.contains("..\\") || fileName.startsWith("..")) {
            return false;
        }

        return true;
    }

    public static String generateValidFilename(String originalFilename) {
        // Generate a new filename based on your desired logic
        // For example, you can replace spaces with underscores
        String cleanedFilename = originalFilename.replaceAll("\\s+", "_");

        // Append a timestamp or a random value to make it unique
        String timestamp = LocalDateTime.now().toString().replace(":", "-");
        String newFilename = timestamp + "_" + cleanedFilename;

        return newFilename;
    }
}
