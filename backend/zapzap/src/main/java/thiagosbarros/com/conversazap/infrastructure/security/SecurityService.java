package thiagosbarros.com.conversazap.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import thiagosbarros.com.conversazap.application.service.UsuarioService;
import thiagosbarros.com.conversazap.domain.model.Usuario;

@Component
public class SecurityService {

    public SecurityService() {
    }
    public Usuario usuarioLogado() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if( authentication instanceof CustomAuthentication customAuth){
            return customAuth.getUsuario();
        }
        return null;
    }
}
