package com.example.hrm.service;

import com.example.hrm.entity.FileAttachment;
import com.example.hrm.exception.AppException;
import com.example.hrm.exception.ErrorCode;
import com.example.hrm.repository.FileAttachmentRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Data
public class FileAttachmentService {

    private final FileUploadService fileUploadService;
    private final FileAttachmentRepository fileAttachmentRepository;

    /**
     * Upload file và lưu thông tin vào database
     * @param file MultipartFile
     * @param refType loại thực thể (ví dụ: "EMPLOYEE")
     * @param refId id của thực thể liên kết
     * @param description mô tả file (optional)
     * @return FileAttachment vừa lưu
     */
    public FileAttachment uploadFile(MultipartFile file, String refType, String refId, String description) {
        // 1. Lưu file lên filesystem
        String savedFileName = fileUploadService.uploadFile(file);

        // 2. Tạo FileAttachment entity
        FileAttachment attachment = FileAttachment.builder()
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .fileUrl(savedFileName)
                .description(description)
                .refType(refType)
                .refId(refId)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        // 3. Lưu vào DB
        return fileAttachmentRepository.save(attachment);
    }

    /**
     * Xóa file cả trên filesystem và DB
     */
    public void deleteFile(String fileId) {
        FileAttachment attachment = fileAttachmentRepository.findById(fileId)
                .orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_FOUND, 404, "File không tồn tại"));

        // Xóa file thực tế
        fileUploadService.deleteFile(attachment.getFileUrl());

        // Xóa trong DB
        attachment.setIsDeleted(true);
        attachment.setDeletedAt(LocalDateTime.now());
        fileAttachmentRepository.save(attachment);
    }
}
