package org.ats.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final Cloudinary cloudinary;

    /**
     * Upload file lên Cloudinary
     *
     * @param file MultipartFile từ request
     * @return Map chứa "url" và "publicId"
     * @throws IOException nếu upload thất bại
     */
    public Map<String, String> uploadFile(MultipartFile file) {
        // Lấy tên file gốc (bỏ phần extension)

        String originalFilename = file.getOriginalFilename();
        String fileNameWithoutExt = originalFilename != null
                ? originalFilename.substring(0, originalFilename.lastIndexOf('.'))
                : "cv_file";

        log.info("Uploading file {}", originalFilename);
        try {
            // Upload lên Cloudinary với resource_type = "raw" (dành cho file PDF, DOCX, ...)
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "resource_type", "raw",           // raw = file không phải ảnh/video
                    "folder", "ats/cv",               // folder trên Cloudinary
                    "public_id", fileNameWithoutExt    // tên file trên Cloudinary
            ));

            String url = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            log.info("Upload thành công: url={}, publicId={}", url, publicId);

            return Map.of(
                    "url", url,
                    "publicId", publicId
            );

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }
}
