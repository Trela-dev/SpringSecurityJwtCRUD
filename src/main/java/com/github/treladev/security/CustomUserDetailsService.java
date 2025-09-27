package com.github.treladev.security;


import com.github.treladev.model.User;
import com.github.treladev.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of UserDetailsService for Spring Security.
 *
 * - Fetches user details from the database based on the username.
 * - Used by Spring Security during authentication.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads user details by username.
     *
     * @param username The username of the user.
     * @return UserDetails object containing user information.
     * @throws UsernameNotFoundException if the user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("Inside loadUserByUsername in CustomUserDetailsService. Username: " + username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Convert the User entity into UserDetails
        return new CustomUserDetails(user);
    }
}
