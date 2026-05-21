package com.carlos.security.core.presentation.controller;

import com.carlos.security.core.application.dto.response.PageResponse;
import com.carlos.security.core.application.dto.response.RoleResponse;
import com.carlos.security.core.application.service.RoleService;
import com.carlos.security.core.shared.constants.SecurityConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(SecurityConstants.ROLES_BASE_PATH)
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    // GET /api/v1/roles?page=0&size=20
    @GetMapping
    public ResponseEntity<PageResponse<RoleResponse>> getAllRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(roleService.getAllRoles(page, size));
    }

    // GET /api/v1/roles/{id}
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable String id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    // DELETE /api/v1/roles/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable String id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/roles/status?active=true&page=0&size=20
    @GetMapping("/status")
    public ResponseEntity<PageResponse<RoleResponse>> getRolesByStatus(
            @RequestParam boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(roleService.getAllByActive(active, page, size));
    }

    // GET /api/v1/roles/system?page=0&size=20
    @GetMapping("/system")
    public ResponseEntity<PageResponse<RoleResponse>> getSystemRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(roleService.getSystemRoles(page, size));
    }

    // GET /api/v1/roles/custom?page=0&size=20
    @GetMapping("/custom")
    public ResponseEntity<PageResponse<RoleResponse>> getCustomRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(roleService.getCustomRoles(page, size));
    }
}
