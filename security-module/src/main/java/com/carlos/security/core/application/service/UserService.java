package com.carlos.security.core.application.service;

import com.carlos.security.core.application.dto.response.PageResponse;
import com.carlos.security.core.application.dto.response.UserResponse;
import com.carlos.security.core.application.mapper.UserMapper;
import com.carlos.security.core.domain.exception.UserNotFoundException;
import com.carlos.security.core.domain.model.User;
import com.carlos.security.core.domain.repository.UserRepository;
import com.carlos.security.core.domain.valueobject.Email;
import com.carlos.security.core.domain.valueobject.Page;
import com.carlos.security.core.domain.valueobject.PageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public PageResponse<UserResponse> getAllUsers(int pageNumber, int pageSize) {
        PageRequest request = PageRequest.of(pageNumber, pageSize, "username", PageRequest.SortDirection.ASC);
        Page<User> page = userRepository.findAll(request);
        return PageResponse.from(page.map(UserMapper::toResponse));
    }

    public UserResponse getUserById(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(UUID.fromString(userId)));
    }

    public Optional<UserResponse> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserMapper::toResponse);
    }

    public Optional<UserResponse> getUserByEmail(String email) {
        return userRepository.findByEmail(Email.of(email))
                .map(UserMapper::toResponse);
    }

    public void deleteUserById(String userId) {
        UUID id = UUID.fromString(userId);
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userRepository.deleteById(id);
    }

    public PageResponse<UserResponse> getAllByRoleName(String roleName, int pageNumber, int pageSize) {
        PageRequest request = PageRequest.of(pageNumber, pageSize, "username", PageRequest.SortDirection.ASC);
        Page<User> page = userRepository.findByRoleName(roleName, request);
        return PageResponse.from(page.map(UserMapper::toResponse));
    }

    public PageResponse<UserResponse> getAllByEnabled(boolean enabled, int pageNumber, int pageSize) {
        PageRequest request = PageRequest.of(pageNumber, pageSize, "username", PageRequest.SortDirection.ASC);
        Page<User> page = userRepository.findByEnabled(enabled, request);
        return PageResponse.from(page.map(UserMapper::toResponse));
    }

    public PageResponse<UserResponse> searchUsers(String searchTerm, int pageNumber, int pageSize) {
        PageRequest request = PageRequest.of(pageNumber, pageSize, "username", PageRequest.SortDirection.ASC);
        Page<User> page = userRepository.searchUsers(searchTerm, request);
        return PageResponse.from(page.map(UserMapper::toResponse));
    }
}
