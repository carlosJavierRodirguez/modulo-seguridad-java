package com.carlos.security.core.infrastructure.security;

import com.carlos.security.core.domain.model.User;
import com.carlos.security.core.domain.repository.UserRepository;
import com.carlos.security.core.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(Email.of(email))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        Set<GrantedAuthority> authorities = new HashSet<>();

        user.getRoleNames().forEach(role ->
                authorities.add(new SimpleGrantedAuthority(role))
        );

        user.getAllPermissions().forEach(permission ->
                authorities.add(new SimpleGrantedAuthority(permission.getName()))
        );

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword().getValue())
                .accountExpired(!user.isAccountNonExpired())
                .accountLocked(user.isAccountLocked())
                .credentialsExpired(!user.isCredentialsNonExpired())
                .disabled(!user.isEnabled())
                .authorities(authorities)
                .build();
    }
}
