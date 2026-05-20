package com.carlos.security.core.application.service;

import com.carlos.security.core.application.dto.response.UserResponse;
import com.carlos.security.core.application.mapper.UserMapper;
import com.carlos.security.core.domain.exception.UserNotFoundException;
import com.carlos.security.core.domain.model.User;
import com.carlos.security.core.domain.repository.UserRepository;
import com.carlos.security.core.domain.valueobject.Email;
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
public class UserService {

    private final UserRepository userRepository;

    // Versión con parámetros — el caller decide la página y el tamaño
    public List<UserResponse> getAllUsers(int pageNumber, int pageSize) {
        PageRequest request = PageRequest.of(pageNumber, pageSize, "username", PageRequest.SortDirection.ASC);
        Page<User> users = userRepository.findAll(request);

        return users.getContent()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    // Versión sin parámetros — usa los defaults de SecurityConstants
    public List<UserResponse> getAllUsers() {
        return getAllUsers(SecurityConstants.DEFAULT_PAGE_NUMBER, SecurityConstants.DEFAULT_PAGE_SIZE);
    }

    // Buscar usuario por ID — lanza excepción si no existe
    public UserResponse getUserById(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(UUID.fromString(userId)));
    }

    // Buscar usuario por username
    public Optional<UserResponse> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserMapper::toResponse);
    }

    // Buscar usuario por email
    public Optional<UserResponse> getUserByEmail(String email) {
        return userRepository.findByEmail(Email.of(email))
                .map(UserMapper::toResponse);
    }

    // Eliminar usuario por ID — verifica existencia antes de eliminar
    public void deleteUserById(String userId) {
        UUID id = UUID.fromString(userId);
        if (!userRepository.findById(id).isPresent()) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    // Buscar usuarios por rol con paginación
    public List<UserResponse> getAllByRoleName(String roleName, int pageNumber, int pageSize) {
        PageRequest request = PageRequest.of(pageNumber, pageSize, "username", PageRequest.SortDirection.ASC);
        return userRepository.findByRoleName(roleName, request)
                .getContent()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    // Buscar usuarios activos o inactivos con paginación
    public List<UserResponse> getAllByEnabled(boolean enabled, int pageNumber, int pageSize) {
        PageRequest request = PageRequest.of(pageNumber, pageSize, "username", PageRequest.SortDirection.ASC);
        return userRepository.findByEnabled(enabled, request)
                .getContent()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    // Búsqueda por texto en username, email o nombre — para barras de búsqueda
    public List<UserResponse> searchUsers(String searchTerm, int pageNumber, int pageSize) {
        PageRequest request = PageRequest.of(pageNumber, pageSize, "username", PageRequest.SortDirection.ASC);
        return userRepository.searchUsers(searchTerm, request)
                .getContent()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }
}
