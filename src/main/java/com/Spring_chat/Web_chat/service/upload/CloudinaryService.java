package com.Spring_chat.Web_chat.service.upload;

import com.Spring_chat.Web_chat.dto.upload.UploadImageResponseDTO;
import com.Spring_chat.Web_chat.config.UploadProperties;
import com.Spring_chat.Web_chat.exception.AppException;
import com.Spring_chat.Web_chat.exception.ErrorCode;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private final UploadProperties uploadProperties;

    public UploadImageResponseDTO uploadImage(MultipartFile file) {
        validateFile(file);

        try {
            String mimeType = normalizeMimeType(file.getContentType());
            String publicId = UUID.randomUUID().toString();
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "resource_type", "auto",
                    "folder", uploadProperties.getCloudinaryFolder(),
                    "public_id", publicId,
                    "use_filename", false,
                    "unique_filename", false,
                    "overwrite", false
            ));

            return UploadImageResponseDTO.builder()
                    .url((String) uploadResult.get("secure_url"))
                    .width(asInteger(uploadResult.get("width")))
                    .height(asInteger(uploadResult.get("height")))
                    .size(asLong(uploadResult.get("bytes")))
                    .mimeType(mimeType)
                    .build();

        } catch (IOException e) {
            log.error("Failed to upload image to Cloudinary", e);
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Failed to upload image: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "File must not be empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !getAllowedMimeTypes().contains(normalizeMimeType(contentType))) {
            throw new AppException(ErrorCode.BUSINESS_RULE_VIOLATED, "Only image files (jpg, jpeg, png, gif, webp) are allowed");
        }
    }

    private String normalizeMimeType(String contentType) {
        return contentType == null ? null : contentType.toLowerCase(Locale.ROOT).trim();
    }

    private Integer asInteger(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        return null;
    }

    private Long asLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return null;
    }

    private java.util.List<String> getAllowedMimeTypes() {
        if (uploadProperties.getAllowedMimeTypes() == null) {
            return java.util.List.of();
        }
        return uploadProperties.getAllowedMimeTypes().stream()
                .filter(value -> value != null && !value.isBlank())
                .map(this::normalizeMimeType)
                .toList();
    }
}
