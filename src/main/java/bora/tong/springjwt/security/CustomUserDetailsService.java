package bora.tong.springjwt.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!"alice".equals(username)) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return new CustomUserDetails(
                "alice",
                passwordEncoder.encode("password"),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}