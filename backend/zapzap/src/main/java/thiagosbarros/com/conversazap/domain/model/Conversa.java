package thiagosbarros.com.conversazap.domain.model;

import jakarta.persistence.*;
import thiagosbarros.com.conversazap.domain.enums.Departamento;
import thiagosbarros.com.conversazap.domain.enums.StatusConversa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "conversas")
public class Conversa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusConversa status;

    @Enumerated(EnumType.STRING)
    private Departamento departamento;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuarioAtual;

    @ManyToOne
    @JoinColumn(name = "usuario_criou_id")
    private Usuario usuarioQueCriou;

    @Column(nullable = false)
    private LocalDateTime dataInicio;

    private LocalDateTime dataFim;

    @OneToMany(mappedBy = "conversa",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Mensagem> mensagens = new ArrayList<>();

    protected Conversa() {
    }

    public Conversa(Cliente cliente, Usuario usuarioAtual, Usuario usuarioQueCriou) {
        this.cliente = cliente;
        this.status = StatusConversa.BOT;
        this.dataInicio = LocalDateTime.now();
        this.usuarioAtual = usuarioAtual;
        this.usuarioQueCriou = usuarioQueCriou;
    }

    public Conversa(Cliente cliente) {
        this.cliente = cliente;
        this.status = StatusConversa.BOT;
        this.dataInicio = LocalDateTime.now();
    }

    public void transferirParaHumano() {
        this.status = StatusConversa.HUMANO;
    }

    public void encerrar() {
        this.status = StatusConversa.ENCERRADA;
        this.dataFim = LocalDateTime.now();
    }

    public Usuario getUsuarioAtual() {
        return usuarioAtual;
    }

    public void assumirConversa(Usuario usuarioAtual) {
        this.usuarioAtual = usuarioAtual;
    }

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public StatusConversa getStatus() {
        return status;
    }

    public List<Mensagem> getMensagens() {
        return mensagens;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void definirDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Conversa conversa = (Conversa) o;
        return Objects.equals(id, conversa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}