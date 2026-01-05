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

import java.util.Collections;
import java.util.List;

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
        String userEmail = username;
        Long tenantId = null;
        // Split only if the username contains '|'
        if (username.contains("|")) {
            String[] parts = username.split("\\|");
            userEmail = parts[0];
            if (parts.length > 1) {
                try {
                    tenantId = Long.parseLong(parts[1]);
                } catch (NumberFormatException e) {
                    throw new UsernameNotFoundException("Invalid tenantId in username");
                }
            }else{
                tenantId = 1L; // default tenantId
            }
        }
        if (tenantId == null) {
            throw new UsernameNotFoundException("TenantId is required for technician login");
        }

        Technician technician = technicianRepository.findByEmailAndTenantId(username,tenantId)
                .orElseThrow(() -> new UsernameNotFoundException("Technician not found with username and tenant id: " + username));

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
