package com.carlos.security.core.domain.repository;

import com.carlos.security.core.domain.model.Role;
import com.carlos.security.core.domain.valueobject.Page;
import com.carlos.security.core.domain.valueobject.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository {

    Page<Role> findAll(PageRequest pageRequest);

    Optional<Role> findById(UUID id);

    Optional<Role> findByName(String name);

    //guarda el rol
    Role save(Role role);

    //elimina rol por id
    void deleteById(UUID id);

    //verifica no existe ese nombre de rol
    boolean existsByName(String name);

    //buscar rol activo
    Page<Role> findByActive(boolean active, PageRequest pageRequest);

    Page<Role> findSystemRoles(PageRequest pageRequest);

    Page<Role> findCustomRoles(PageRequest pageRequest);

    long count();

}