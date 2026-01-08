package thiagosbarros.com.conversazap.interfaces.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import thiagosbarros.com.conversazap.domain.enums.Departamento;
import thiagosbarros.com.conversazap.domain.enums.Role;

public class UsuarioDTO {
    @NotBlank
    private String login;
    @NotBlank
    private String senha;
    @Email
    private String email;

    private Departamento departamento;

    private Role role;

    private Long idEmpresa;

    public UsuarioDTO(){

    }

    public UsuarioDTO(String login, String senha, String email, Role role, Long idEmpresa, Departamento departamento) {
        this.login = login;
        this.senha = senha;
        this.email = email;
        this.role = role;
        this.idEmpresa = idEmpresa;
        this.departamento = departamento;

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
    public Departamento getDepartamento() {
        return departamento;
    }
}
