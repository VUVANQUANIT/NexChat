package com.Spring_chat.Web_chat.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

import java.util.List;

@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties {

    private DataSize maxFileSize;
    private List<String> allowedMimeTypes;
    private String cloudinaryFolder;

    public DataSize getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(DataSize maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public List<String> getAllowedMimeTypes() {
        return allowedMimeTypes;
    }

    public void setAllowedMimeTypes(List<String> allowedMimeTypes) {
        this.allowedMimeTypes = allowedMimeTypes;
    }

    public String getCloudinaryFolder() {
        return cloudinaryFolder;
    }

    public void setCloudinaryFolder(String cloudinaryFolder) {
        this.cloudinaryFolder = cloudinaryFolder;
    }
}
