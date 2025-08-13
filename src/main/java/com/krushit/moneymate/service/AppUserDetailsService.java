package com.krushit.moneymate.service;

import java.util.Collections;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.krushit.moneymate.entity.User;
import com.krushit.moneymate.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User existingProfile = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + email));
        return org.springframework.security.core.userdetails.User.builder()
                .username(existingProfile.getEmail())
                .password(existingProfile.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}
