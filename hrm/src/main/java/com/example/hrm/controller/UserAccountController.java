package com.example.hrm.controller;

import com.example.hrm.dto.request.UserAccountRequest;
import com.example.hrm.dto.response.UserAccountResponse;
import com.example.hrm.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.api-prefix}/user-accounts")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;

    @PostMapping
    public ResponseEntity<UserAccountResponse> createUserAccount(
            @Valid @RequestBody UserAccountRequest request
    ) {
        var response = userAccountService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<UserAccountResponse>> getAllUserAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var response = userAccountService.getAll(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAccountResponse> getUserAccountById(
            @PathVariable String id
    ) {
        var response = userAccountService.getById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserAccountResponse> updateUserAccount(
            @PathVariable String id,
            @Valid @RequestBody UserAccountRequest request
    ) {
        var response = userAccountService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserAccount(
            @PathVariable String id
    ) {
        userAccountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
