package thiagosbarros.com.conversazap.domain.model;
import jakarta.persistence.*;
import thiagosbarros.com.conversazap.domain.enums.Departamento;
import thiagosbarros.com.conversazap.domain.enums.Role;

import java.util.Objects;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String login;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    private Departamento departamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private Boolean ativo = true;

    @ManyToOne(optional = false)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    protected Usuario() {
    }

    public Usuario(String login, String email, String senha, Role role, Empresa empresa) {
        this.login = login;
        this.email = email;
        this.senha = senha;
        this.role = role;
        this.empresa = empresa;
    }


    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public String getSenha() {
        return senha;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar(){
        this.ativo = false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
