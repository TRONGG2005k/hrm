package com.example.hrm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileAttachmentResponse {

    String id;

    String fileName;

    String fileType;

    String fileUrl;

    Long fileSize;

    String description;

    String refType;

    String refId;

    LocalDateTime createdAt;

    Boolean isDeleted;

    LocalDateTime deletedAt;
}
