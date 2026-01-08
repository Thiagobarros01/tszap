package thiagosbarros.com.conversazap.domain.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "etapas_bot")
public class EtapaBot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String mensagem;

    private boolean inicial;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @OneToMany(mappedBy = "etapaOrigem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OpcaoBot> opcoes;

    public EtapaBot() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public boolean isInicial() {
        return inicial;
    }

    public void setInicial(boolean inicial) {
        this.inicial = inicial;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public List<OpcaoBot> getOpcoes() {
        return opcoes;
    }

    public void setOpcoes(List<OpcaoBot> opcoes) {
        this.opcoes = opcoes;
    }
}
