package opus.social.app.reporteai.adapters.security;

import opus.social.app.reporteai.infrastructure.persistence.entity.AuthUserJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.repository.AuthUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Serviço para carregar detalhes do usuário
 * Implementa UserDetailsService do Spring Security
 * Carrega usuários do banco de dados
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthUserRepository authUserRepository;

    public CustomUserDetailsService(AuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUserJpaEntity user = authUserRepository.findByUsernameWithRoles(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        // Validar se usuário está ativo
        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("Usuário desativado: " + username);
        }

        // Validar se usuário está bloqueado
        if (user.getIsLocked()) {
            throw new UsernameNotFoundException("Usuário bloqueado: " + username);
        }

        // Converter roles do banco para SimpleGrantedAuthority
        var authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
            .toList();

        return User.builder()
            .username(user.getUsername())
            .password(user.getPasswordHash())
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(user.getIsLocked())
            .credentialsExpired(false)
            .disabled(!user.getIsActive())
            .build();
    }
}
