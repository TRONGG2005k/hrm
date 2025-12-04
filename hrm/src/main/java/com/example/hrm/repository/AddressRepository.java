package com.example.hrm.repository;

import com.example.hrm.entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
    Page<Address> findByIsDeletedFalse(Pageable pageable);
}
