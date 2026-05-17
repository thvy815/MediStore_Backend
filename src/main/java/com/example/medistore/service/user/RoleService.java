package com.example.medistore.service.user;

import com.example.medistore.entity.user.Permission;
import com.example.medistore.entity.user.Role;
import com.example.medistore.repository.user.PermissionRepository;
import com.example.medistore.repository.user.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    public Role updateRole(UUID id, Role updatedRole) {
        Role role = getRoleById(id);
        role.setName(updatedRole.getName());
        return roleRepository.save(role);
    }

    public void deleteRole(UUID id) {
        roleRepository.deleteById(id);
    }

    @Transactional
    public Role assignPermission(UUID roleId, UUID permissionId) {

        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new RuntimeException("Role not found"));

        Permission permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new RuntimeException("Permission not found"));

        role.getPermissions().add(permission);

        return roleRepository.save(role);
    }

    @Transactional
    public Role removePermission(UUID roleId, UUID permissionId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        role.getPermissions().remove(permission);

        return roleRepository.save(role);
    }
}

