package com.example.medistore.service.user;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.medistore.entity.user.Permission;
import com.example.medistore.repository.user.PermissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepo;

    public Permission create(String name) {

        if(permissionRepo.findByName(name).isPresent()) {
            throw new RuntimeException("Permission already exists");
        }

        Permission permission = Permission.builder()
                .name(name)
                .build();

        return permissionRepo.save(permission);
    }

    public List<Permission> getAll() {
        return permissionRepo.findAll();
    }
}
