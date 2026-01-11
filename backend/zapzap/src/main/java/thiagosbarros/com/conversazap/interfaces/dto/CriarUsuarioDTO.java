package thiagosbarros.com.conversazap.interfaces.dto;

import thiagosbarros.com.conversazap.domain.enums.Departamento;
import thiagosbarros.com.conversazap.domain.enums.Role;

public class CriarUsuarioDTO {
    private String login;
    private String email;
    private String senha;
    private Role role;
    private Departamento departamento;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }
}
