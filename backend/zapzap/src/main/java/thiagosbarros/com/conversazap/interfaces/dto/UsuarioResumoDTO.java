package thiagosbarros.com.conversazap.interfaces.dto;

import thiagosbarros.com.conversazap.domain.enums.Departamento;
import thiagosbarros.com.conversazap.domain.enums.Role;

public record UsuarioResumoDTO(Long id, String login, String email, Role role, Departamento departamento) {
}