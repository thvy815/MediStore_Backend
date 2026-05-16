package com.example.medistore.service.prescription;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) {
        try {
            // check empty file
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            // check file size
            long maxSize = 10 * 1024 * 1024; // 10MB

            if (file.getSize() > maxSize) {
                throw new RuntimeException("File size must be less than 10MB");
            }

            // check file type
            String contentType = file.getContentType();

            if (contentType == null ||
                    (!contentType.startsWith("image/")
                    && !contentType.equals("application/pdf"))) {

                throw new RuntimeException("Only image or PDF files are allowed");
            }

            Map<?, ?> uploadResult = cloudinary.uploader()
                    .upload(file.getBytes(),
                            Map.of("folder", "medistore/prescriptions",
                                   "resource_type", "auto",
                                   "use_filename", false,
                                   "unique_filename", true
                            ));

            return uploadResult.get("secure_url").toString();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Upload file failed");
        }
    }
}