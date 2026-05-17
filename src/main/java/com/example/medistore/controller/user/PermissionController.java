package com.example.medistore.controller.user;

import com.example.medistore.entity.user.Permission;
import com.example.medistore.service.user.PermissionService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public Permission createPermission(
            @RequestParam String name
    ) {
        return permissionService.create(name);
    }

    @GetMapping
    public List<Permission> getAllPermissions() {
        return permissionService.getAll();
    }
}