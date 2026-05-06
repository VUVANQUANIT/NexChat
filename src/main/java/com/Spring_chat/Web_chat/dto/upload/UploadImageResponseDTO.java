package com.Spring_chat.Web_chat.dto.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadImageResponseDTO {
    private String url;
    private Integer width;
    private Integer height;
    private Long size;
    private String mimeType;
}
