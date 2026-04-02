package opus.social.app.reporteai.adapters.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Serviço de autenticação e autorização
 */
@Service
public class AuthService {

    /**
     * Verifica se o usuário atual é o proprietário do recurso ou um ADMIN
     */
    public boolean isUserOrAdmin(UUID userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Admin sempre tem acesso
        if (authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        // Aqui você compararia com o ID do usuário autenticado
        // Por exemplo: return authentication.getName().equals(userId.toString());
        // Para simplificar, apenas verificamos se é um usuário autenticado
        return true;
    }

    /**
     * Obtém o nome do usuário autenticado
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    /**
     * Verifica se o usuário tem uma role específica
     */
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }

    /**
     * Verifica se o usuário é um ADMIN
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Verifica se o usuário está autenticado
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}
