package com.carlos.security.core.application.service;

import com.carlos.security.core.application.dto.response.RoleResponse;
import com.carlos.security.core.application.mapper.RoleMapper;
import com.carlos.security.core.domain.exception.RoleNotFoundException;
import com.carlos.security.core.domain.model.Role;
import com.carlos.security.core.domain.repository.RoleRepository;
import com.carlos.security.core.domain.valueobject.Page;
import com.carlos.security.core.domain.valueobject.PageRequest;
import com.carlos.security.core.shared.constants.SecurityConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    // Versión con parámetros — el caller decide la página y el tamaño
    public List<RoleResponse> getAllRoles(int pageNumber, int pageSize) {
        PageRequest request = PageRequest.of(pageNumber, pageSize, "name", PageRequest.SortDirection.ASC);
        Page<Role> roles = roleRepository.findAll(request);

        return roles.getContent()
                .stream()
                .map(RoleMapper::toResponse)
                .toList();
    }

    // Versión sin parámetros — usa los defaults de SecurityConstants
    public List<RoleResponse> getAllRoles() {
        return getAllRoles(SecurityConstants.DEFAULT_PAGE_NUMBER, SecurityConstants.DEFAULT_PAGE_SIZE);
    }

    // Buscar rol por ID — lanza excepción si no existe
    public RoleResponse getRoleById(String roleId) {
        return roleRepository.findById(UUID.fromString(roleId))
                .map(RoleMapper::toResponse)
                .orElseThrow(() -> new RoleNotFoundException(UUID.fromString(roleId)));
    }

    // Buscar rol por nombre — devuelve Optional porque puede no existir
    public Optional<RoleResponse> getRoleByName(String name) {
        return roleRepository.findByName(name)
                .map(RoleMapper::toResponse);
    }

    // Eliminar rol por ID — verifica existencia antes de eliminar
    public void deleteRole(String roleId) {
        UUID id = UUID.fromString(roleId);
        if (!roleRepository.findById(id).isPresent()) {
            throw new RoleNotFoundException(id);
        }
        roleRepository.deleteById(id);
    }

    // Buscar roles activos o inactivos con paginación
    public List<RoleResponse> getAllByActive(boolean active, int pageNumber, int pageSize) {
        PageRequest request = PageRequest.of(pageNumber, pageSize, "name", PageRequest.SortDirection.ASC);
        return roleRepository.findByActive(active, request)
                .getContent()
                .stream()
                .map(RoleMapper::toResponse)
                .toList();
    }

    // Buscar roles del sistema (ROLE_SYSTEM_*) con paginación
    public List<RoleResponse> getSystemRoles(int pageNumber, int pageSize) {
        PageRequest request = PageRequest.of(pageNumber, pageSize, "name", PageRequest.SortDirection.ASC);
        return roleRepository.findSystemRoles(request)
                .getContent()
                .stream()
                .map(RoleMapper::toResponse)
                .toList();
    }

    // Buscar roles personalizados (no del sistema) con paginación
    public List<RoleResponse> getCustomRoles(int pageNumber, int pageSize) {
        PageRequest request = PageRequest.of(pageNumber, pageSize, "name", PageRequest.SortDirection.ASC);
        return roleRepository.findCustomRoles(request)
                .getContent()
                .stream()
                .map(RoleMapper::toResponse)
                .toList();
    }
}
