package com.example.hrm.repository;

import com.example.hrm.entity.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
    Page<UserAccount> findByIsDeletedFalse(Pageable pageable);
    UserAccount findByUsernameAndIsDeletedFalse(String username);
}
