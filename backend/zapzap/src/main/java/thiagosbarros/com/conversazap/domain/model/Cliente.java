package thiagosbarros.com.conversazap.domain.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "clientes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"telefone", "empresa_id"})
        }
)
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String telefone;

    private String nome;

    @ManyToOne(optional = false)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    protected Cliente() {
    }

    public Cliente(String telefone, Empresa empresa) {
        this.telefone = telefone;
        this.empresa = empresa;
    }

    public Long getId() {
        return id;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getNome() {
        return nome;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void definirNome(String nome) {
        this.nome = nome;
    }
}