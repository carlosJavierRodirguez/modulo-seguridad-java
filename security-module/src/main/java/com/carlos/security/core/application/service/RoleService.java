package com.carlos.security.core.application.service;

import com.carlos.security.core.application.dto.response.PageResponse;
import com.carlos.security.core.application.dto.response.RoleResponse;
import com.carlos.security.core.application.mapper.RoleMapper;
import com.carlos.security.core.domain.exception.RoleNotFoundException;
import com.carlos.security.core.domain.model.Role;
import com.carlos.security.core.domain.repository.RoleRepository;
import com.carlos.security.core.domain.valueobject.Page;
import com.carlos.security.core.domain.valueobject.PageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public PageResponse<RoleResponse> getAllRoles(int pageNumber, int pageSize) {
        PageRequest request = PageRequest.of(pageNumber, pageSize, "name", PageRequest.SortDirection.ASC);
        Page<Role> page = roleRepository.findAll(request);
        return PageResponse.from(page.map(RoleMapper::toResponse));
    }

    public RoleResponse getRoleById(String roleId) {
        return roleRepository.findById(UUID.fromString(roleId))
                .map(RoleMapper::toResponse)
                .orElseThrow(() -> new RoleNotFoundException(UUID.fromString(roleId)));
    }

    public Optional<RoleResponse> getRoleByName(String name) {
        return roleRepository.findByName(name)
                .map(RoleMapper::toResponse);
    }

    public void deleteRole(String roleId) {
        UUID id = UUID.fromString(roleId);
        roleRepository.findById(id).orElseThrow(() -> new RoleNotFoundException(id));
        roleRepository.deleteById(id);
    }

    public PageResponse<RoleResponse> getAllByActive(boolean active, int pageNumber, int pageSize) {
        PageRequest request = PageRequest.of(pageNumber, pageSize, "name", PageRequest.SortDirection.ASC);
        Page<Role> page = roleRepository.findByActive(active, request);
        return PageResponse.from(page.map(RoleMapper::toResponse));
    }

    public PageResponse<RoleResponse> getSystemRoles(int pageNumber, int pageSize) {
        PageRequest request = PageRequest.of(pageNumber, pageSize, "name", PageRequest.SortDirection.ASC);
        Page<Role> page = roleRepository.findSystemRoles(request);
        return PageResponse.from(page.map(RoleMapper::toResponse));
    }

    public PageResponse<RoleResponse> getCustomRoles(int pageNumber, int pageSize) {
        PageRequest request = PageRequest.of(pageNumber, pageSize, "name", PageRequest.SortDirection.ASC);
        Page<Role> page = roleRepository.findCustomRoles(request);
        return PageResponse.from(page.map(RoleMapper::toResponse));
    }
}
