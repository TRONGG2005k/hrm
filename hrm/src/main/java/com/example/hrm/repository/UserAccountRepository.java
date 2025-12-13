package com.example.hrm.repository;

import com.example.hrm.entity.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
    @EntityGraph(attributePaths = {"employee"})
    Page<UserAccount> findByIsDeletedFalse(Pageable pageable);
    Optional<UserAccount> findByUsernameAndIsDeletedFalse(String username);
    Optional<UserAccount> findByIdAndIsDeletedFalse(String id);
}
