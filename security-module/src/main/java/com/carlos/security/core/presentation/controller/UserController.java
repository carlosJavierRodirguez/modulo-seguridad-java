package com.carlos.security.core.presentation.controller;

import com.carlos.security.core.application.dto.response.PageResponse;
import com.carlos.security.core.application.dto.response.UserResponse;
import com.carlos.security.core.application.service.UserService;
import com.carlos.security.core.shared.constants.SecurityConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(SecurityConstants.USERS_BASE_PATH)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /api/v1/users?page=0&size=20
    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }

    // GET /api/v1/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // DELETE /api/v1/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable String id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/users/search?term=...&page=0&size=20
    @GetMapping("/search")
    public ResponseEntity<PageResponse<UserResponse>> searchUsers(
            @RequestParam String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userService.searchUsers(term, page, size));
    }

    // GET /api/v1/users/role/{roleName}?page=0&size=20
    @GetMapping("/role/{roleName}")
    public ResponseEntity<PageResponse<UserResponse>> getUsersByRole(
            @PathVariable String roleName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userService.getAllByRoleName(roleName, page, size));
    }

    // GET /api/v1/users/status?enabled=true&page=0&size=20
    @GetMapping("/status")
    public ResponseEntity<PageResponse<UserResponse>> getUsersByStatus(
            @RequestParam boolean enabled,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userService.getAllByEnabled(enabled, page, size));
    }
}
