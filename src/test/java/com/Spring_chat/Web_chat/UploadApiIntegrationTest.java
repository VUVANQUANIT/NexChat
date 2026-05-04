package com.Spring_chat.Web_chat;

import com.Spring_chat.Web_chat.dto.upload.UploadImageResponseDTO;
import com.Spring_chat.Web_chat.service.upload.CloudinaryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "jwt.secret=0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF",
        "jwt.expiration=86400000",
        "jwt.refresh-expiration=604800000",
        "spring.main.allow-bean-definition-overriding=true"
})
@AutoConfigureMockMvc
class UploadApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CloudinaryService cloudinaryService;

    @TestConfiguration
    static class TestBeans {
        @Bean
        CloudinaryService cloudinaryService() {
            return mock(CloudinaryService.class);
        }
    }

    @Test
    @WithMockUser(username = "uploader", roles = "USER")
    void uploadImage_shouldReturnCreatedResponseWithExpectedJsonShape() throws Exception {
        when(cloudinaryService.uploadImage(any())).thenReturn(UploadImageResponseDTO.builder()
                .url("https://cdn.example.com/images/avatar.png")
                .width(1920)
                .height(1080)
                .size(204800L)
                .mimeType("image/png")
                .build());

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                "image/png",
                new byte[] {1, 2, 3}
        );

        mockMvc.perform(multipart("/api/uploads/images").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Image uploaded"))
                .andExpect(jsonPath("$.data.url").value("https://cdn.example.com/images/avatar.png"))
                .andExpect(jsonPath("$.data.width").value(1920))
                .andExpect(jsonPath("$.data.height").value(1080))
                .andExpect(jsonPath("$.data.size").value(204800))
                .andExpect(jsonPath("$.data.mimeType").value("image/png"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "uploader", roles = "USER")
    void uploadImage_whenUploadSizeExceeded_shouldReturnValidationErrorResponse() throws Exception {
        when(cloudinaryService.uploadImage(any())).thenThrow(new MaxUploadSizeExceededException(5 * 1024 * 1024L));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                "image/png",
                new byte[] {1, 2, 3}
        );

        mockMvc.perform(multipart("/api/uploads/images").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").doesNotExist())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/api/uploads/images"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}
