package thiagosbarros.com.conversazap.infrastructure.security;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import thiagosbarros.com.conversazap.domain.model.Usuario;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;

    public TokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String gerarToken(Usuario usuario) {
        Instant now = Instant.now();

        String scope = usuario.getRole().name();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("conversazap-api")
                .issuedAt(now)
                .expiresAt(now.plus(2, ChronoUnit.HOURS))
                .subject(usuario.getLogin())
                .claim("scope", scope)
                .claim("usuarioId", usuario.getId())
                .claim("empresaId", usuario.getEmpresa().getId())
                .build();

                return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

}
