package com.Spring_chat.Web_chat.service.upload;

import com.Spring_chat.Web_chat.config.UploadProperties;
import com.Spring_chat.Web_chat.dto.upload.UploadImageResponseDTO;
import com.Spring_chat.Web_chat.exception.AppException;
import com.cloudinary.Cloudinary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Cloudinary cloudinary;

    private UploadProperties uploadProperties;
    private CloudinaryService cloudinaryService;

    @BeforeEach
    void setUp() {
        uploadProperties = new UploadProperties();
        uploadProperties.setMaxFileSize(DataSize.ofMegabytes(5));
        uploadProperties.setAllowedMimeTypes(List.of(
                "image/jpeg",
                "image/png",
                "image/gif",
                "image/webp"
        ));
        uploadProperties.setCloudinaryFolder("spring_chat_uploads");
        cloudinaryService = new CloudinaryService(cloudinary, uploadProperties);
    }

    @Test
    void uploadImage_shouldReturnMimeTypeFromFileAndUseConfiguredFolder() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                "image/png",
                new byte[] {1, 2, 3}
        );

        when(cloudinary.uploader().upload(any(byte[].class), anyMap())).thenReturn(Map.of(
                "secure_url", "https://cdn.example.com/avatar.png",
                "width", 1920,
                "height", 1080,
                "bytes", 204800L
        ));

        UploadImageResponseDTO result = cloudinaryService.uploadImage(file);

        assertEquals("https://cdn.example.com/avatar.png", result.getUrl());
        assertEquals(1920, result.getWidth());
        assertEquals(1080, result.getHeight());
        assertEquals(204800L, result.getSize());
        assertEquals("image/png", result.getMimeType());

        verify(cloudinary.uploader()).upload(any(byte[].class), anyMap());
    }

    @Test
    void uploadImage_shouldRejectFilesLargerThanConfiguredLimit() {
        uploadProperties.setMaxFileSize(DataSize.ofBytes(2));
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                "image/png",
                new byte[] {1, 2, 3}
        );

        AppException ex = assertThrows(AppException.class, () -> cloudinaryService.uploadImage(file));

        assertEquals("File size must not exceed 2B", ex.getMessage());
    }
}
