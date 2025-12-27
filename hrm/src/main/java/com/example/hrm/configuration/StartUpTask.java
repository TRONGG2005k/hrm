package com.example.hrm.configuration;

import com.example.hrm.entity.Role;
import com.example.hrm.entity.UserAccount;
import com.example.hrm.enums.UserStatus;
import com.example.hrm.repository.RoleRepository;
import com.example.hrm.repository.UserAccountRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class StartUpTask {
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserAccountRepository userAccountRepository;

    @PostConstruct
    public void init(){
        Role role = roleRepository.findByNameAndIsDeletedFalse(com.example.hrm.enums.Role.ROLE_ADMIN.name()).orElseGet(
                ()->roleRepository.save(
                        Role.builder()
                                .name("ROLE_ADMIN")
                                .build()
                )
        );

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        userAccountRepository.findByUsernameAndIsDeletedFalseAndStatus("admin", UserStatus.ACTIVE).orElseGet(
                () -> userAccountRepository.save(
                        UserAccount.builder()
                                .username("admin")
                                .password(passwordEncoder.encode("admin"))
                                .roles(roles)
                                .status(UserStatus.ACTIVE)
                                .build()
                )
        );
    }
}
