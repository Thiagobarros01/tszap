package thiagosbarros.com.conversazap.infrastructure.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thiagosbarros.com.conversazap.application.usecase.RealizarLoginUseCase;
import thiagosbarros.com.conversazap.infrastructure.security.CustomAuthentication;
import thiagosbarros.com.conversazap.infrastructure.security.TokenService;
import thiagosbarros.com.conversazap.interfaces.dto.LoginRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RealizarLoginUseCase  realizarLoginUseCase;

    public AuthController(RealizarLoginUseCase realizarLoginUseCase) {
        this.realizarLoginUseCase = realizarLoginUseCase;
    }

    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok().body(realizarLoginUseCase.executar(loginRequest));
    }
}
