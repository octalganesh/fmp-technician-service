package com.octal.fsm.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TechnicianAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String username = request.getHeader("username");
//        if (StringUtils.isNotEmpty(username)) {
//            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                    userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }

        filterChain.doFilter(request, response);
    }

}
