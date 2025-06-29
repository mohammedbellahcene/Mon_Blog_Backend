package com.blog.api.security;

import com.blog.api.entity.User;
import com.blog.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        // Essayer d'abord par email
        User user = userRepository.findByEmail(emailOrUsername)
                .orElse(null);
        
        // Si pas trouvé par email, essayer par username
        if (user == null) {
            user = userRepository.findByUsername(emailOrUsername)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email or username: " + emailOrUsername));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Arrays.stream(user.getRoles())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
    }
} 