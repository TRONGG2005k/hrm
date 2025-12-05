package com.example.hrm.service;

import com.example.hrm.exception.AppException;
import com.example.hrm.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.upload.max-size:52428800}")
    private long maxFileSize;

    private static final String[] ALLOWED_EXTENSIONS = {
            "jpg", "jpeg", "png", "gif", "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx"
    };

    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, 400, "File không được để trống");
        }

        // Kiểm tra kích thước file
        if (file.getSize() > maxFileSize) {
            throw new AppException(ErrorCode.FILE_SIZE_EXCEEDED, 400,
                    "Kích thước file vượt quá giới hạn: " + (maxFileSize / 1024 / 1024) + "MB");
        }

        // Kiểm tra định dạng file
        String fileName = file.getOriginalFilename();
        if (fileName == null || !isAllowedFileType(fileName)) {
            throw new AppException(ErrorCode.FILE_INVALID_TYPE, 400, "Loại file không được hỗ trợ");
        }

        try {
            // Tạo thư mục nếu chưa tồn tại
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Tạo tên file duy nhất
            String uniqueFileName = generateUniqueFileName(fileName);
            Path filePath = uploadPath.resolve(uniqueFileName);

            // Lưu file
            Files.write(filePath, file.getBytes());

            return uniqueFileName;
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, 500, "Lỗi khi lưu file: " + e.getMessage());
        }
    }

    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        try {
            Path filePath = Paths.get(uploadDir, fileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, 500, "Lỗi khi xóa file: " + e.getMessage());
        }
    }

    public byte[] downloadFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND, 404, "Tên file không hợp lệ");
        }

        try {
            Path filePath = Paths.get(uploadDir, fileName);
            if (!Files.exists(filePath)) {
                throw new AppException(ErrorCode.FILE_NOT_FOUND, 404, "File không tìm thấy");
            }

            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, 500, "Lỗi khi đọc file: " + e.getMessage());
        }
    }

    private boolean isAllowedFileType(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equals(extension)) {
                return true;
            }
        }
        return false;
    }

    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(lastDot + 1);
        }
        return "";
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + extension;
    }
}
