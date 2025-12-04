package com.example.hrm.repository;

import com.example.hrm.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {
    Page<Contact> findByIsDeletedFalse(Pageable pageable);
    Page<Contact> findByEmployeeIdAndIsDeletedFalse(String employeeId, Pageable pageable);
}
