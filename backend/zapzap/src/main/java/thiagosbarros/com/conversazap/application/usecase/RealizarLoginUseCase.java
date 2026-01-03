package thiagosbarros.com.conversazap.application.usecase;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import thiagosbarros.com.conversazap.infrastructure.security.CustomAuthentication;
import thiagosbarros.com.conversazap.infrastructure.security.TokenService;
import thiagosbarros.com.conversazap.interfaces.dto.LoginRequest;

@Component
public class RealizarLoginUseCase {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public RealizarLoginUseCase(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public String executar(LoginRequest request) {
        UsernamePasswordAuthenticationToken loginData =
                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getSenha());

        Authentication authentication = authenticationManager.authenticate(loginData);
        CustomAuthentication customAuth = (CustomAuthentication) authentication;

        return tokenService.gerarToken(customAuth.getUsuario());
    }

}
