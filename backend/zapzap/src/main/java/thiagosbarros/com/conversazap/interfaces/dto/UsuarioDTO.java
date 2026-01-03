package thiagosbarros.com.conversazap.interfaces.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import thiagosbarros.com.conversazap.domain.enums.Role;

public class UsuarioDTO {
    @NotBlank
    private String login;
    @NotBlank
    private String senha;
    @Email
    private String email;

    private Role role;

    private Long idEmpresa;

    public UsuarioDTO(String login, String senha, String email, Role role, Long idEmpresa) {
        this.login = login;
        this.senha = senha;
        this.email = email;
        this.role = role;
        this.idEmpresa = idEmpresa;
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
    public Long getIdEmpresa() {
        return idEmpresa;
    }
}
