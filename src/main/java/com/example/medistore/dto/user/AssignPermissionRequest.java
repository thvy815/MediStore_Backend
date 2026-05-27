package com.example.medistore.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AssignPermissionRequest {

    private UUID roleId;
    private UUID permissionId;
}