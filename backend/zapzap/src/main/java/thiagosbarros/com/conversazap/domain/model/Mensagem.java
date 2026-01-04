package thiagosbarros.com.conversazap.domain.model;

import jakarta.persistence.*;
import thiagosbarros.com.conversazap.domain.enums.OrigemMensagem;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "mensagens")
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "conversa_id")
    private Conversa conversa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrigemMensagem origem;

    @Column(nullable = false, length = 1000)
    private String texto;

    @Column(nullable = false)
    private LocalDateTime data;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private boolean lida = false;

    protected Mensagem() {
    }

    public Mensagem(Conversa conversa, OrigemMensagem origem, String texto, Usuario usuario) {
        this.conversa = conversa;
        this.origem = origem;
        this.texto = texto;
        this.data = LocalDateTime.now();
        this.usuario = usuario;
    }

    public Mensagem(Conversa conversa, OrigemMensagem origem, String texto) {
        this.conversa = conversa;
        this.origem = origem;
        this.texto = texto;
        this.data = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public OrigemMensagem getOrigem() {
        return origem;
    }

    public String getTexto() {
        return texto;
    }

    public LocalDateTime getData() {
        return data;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public boolean isLida() {
        return lida;
    }

    public void marcarComoLida(boolean lida) {
        this.lida = lida;
    }

}
