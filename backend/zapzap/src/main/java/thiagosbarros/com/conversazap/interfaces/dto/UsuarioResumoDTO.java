package thiagosbarros.com.conversazap.interfaces.dto;

import thiagosbarros.com.conversazap.domain.enums.Role;

public record UsuarioResumoDTO(String login, String email, Role role) {
}
