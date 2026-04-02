package opus.social.app.reporteai.adapters.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Serviço para carregar detalhes do usuário
 * Implementa UserDetailsService do Spring Security
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Simulação de banco de dados de usuários (em produção, usar banco de dados real)
    private final Map<String, UserDetails> users = new HashMap<>();

    public CustomUserDetailsService() {
        initializeUsers();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }
        return user;
    }

    /**
     * Inicializa usuários de exemplo
     * Em produção, isso viria do banco de dados
     */
    private void initializeUsers() {
        // Admin user
        users.put("admin", User.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin123"))
            .authorities(Arrays.asList(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_USER")
            ))
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build());

        // Analyst user
        users.put("analyst", User.builder()
            .username("analyst")
            .password(passwordEncoder.encode("analyst123"))
            .authorities(Arrays.asList(
                new SimpleGrantedAuthority("ROLE_ANALYST"),
                new SimpleGrantedAuthority("ROLE_USER")
            ))
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build());

        // Regular user
        users.put("user", User.builder()
            .username("user")
            .password(passwordEncoder.encode("user123"))
            .authorities(Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER")
            ))
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build());

        // Validator user
        users.put("validator", User.builder()
            .username("validator")
            .password(passwordEncoder.encode("validator123"))
            .authorities(Arrays.asList(
                new SimpleGrantedAuthority("ROLE_VALIDATOR"),
                new SimpleGrantedAuthority("ROLE_USER")
            ))
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build());
    }

    /**
     * Método auxiliar para criar um novo usuário
     * Em produção, seria persistido no banco de dados
     */
    public void createUser(String username, String password, String... roles) {
        if (users.containsKey(username)) {
            throw new RuntimeException("Usuário já existe: " + username);
        }

        users.put(username, User.builder()
            .username(username)
            .password(passwordEncoder.encode(password))
            .authorities(
                Arrays.stream(roles)
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .toList()
            )
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build());
    }
}
