package com.octal.fsm.jwt;

import com.octal.fsm.entities.Technician;

import com.octal.fsm.repositories.TechnicianRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CustomUserDetailsService implements UserDetailsService and overrides its method
 * which is used to retrieve the user's authentication and authorization information
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private TechnicianRepository technicianRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Technician technician = technicianRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Technician not found with username: " + username));

         List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_TECHNICIAN")
        );

        return new org.springframework.security.core.userdetails.User(
                technician.getEmail(),
                technician.getPassword(),
                true, true, true, true,
                authorities
        );
    }
}
