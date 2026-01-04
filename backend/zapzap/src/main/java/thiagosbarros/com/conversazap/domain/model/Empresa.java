package thiagosbarros.com.conversazap.domain.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "empresas")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "telefone_whatsapp", nullable = false, unique = true)
    private String telefoneWhatsApp;

    @Column(nullable = false)
    private Boolean ativa = true;


    protected Empresa() {
    }

    public Empresa(String nome, String telefoneWhatsApp) {
        this.nome = nome;
        this.telefoneWhatsApp = telefoneWhatsApp;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefoneWhatsApp() {
        return telefoneWhatsApp;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void desativar() {
        this.ativa = false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Empresa empresa = (Empresa) o;
        return Objects.equals(id, empresa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}