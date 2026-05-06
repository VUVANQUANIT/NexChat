package com.Spring_chat.Web_chat.controller.upload;

import com.Spring_chat.Web_chat.dto.ApiResponse;
import com.Spring_chat.Web_chat.dto.upload.UploadImageResponseDTO;
import com.Spring_chat.Web_chat.service.upload.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final CloudinaryService cloudinaryService;

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UploadImageResponseDTO>> uploadImage(@RequestParam("file") MultipartFile file) {
        UploadImageResponseDTO data = cloudinaryService.uploadImage(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, "Image uploaded", data, Instant.now())
        );
    }
}
