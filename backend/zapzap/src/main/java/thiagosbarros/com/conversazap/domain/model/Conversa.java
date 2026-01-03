package thiagosbarros.com.conversazap.domain.model;

import jakarta.persistence.*;
import thiagosbarros.com.conversazap.domain.enums.StatusConversa;

import java.time.LocalDateTime;

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

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuarioAtual;

    @ManyToOne
    @JoinColumn(name = "usuario_criou_id")
    private Usuario usuarioQueCriou;

    @Column(nullable = false)
    private LocalDateTime dataInicio;

    private LocalDateTime dataFim;

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
}