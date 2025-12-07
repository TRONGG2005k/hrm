package com.example.hrm.repository;

import com.example.hrm.entity.FileAttachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileAttachmentRepository extends JpaRepository<FileAttachment, String> {
    Page<FileAttachment> findByIsDeletedFalse(Pageable pageable);
    List<FileAttachment> findByRefTypeAndRefIdAndIsDeletedFalse(String refType, String refId);
}
