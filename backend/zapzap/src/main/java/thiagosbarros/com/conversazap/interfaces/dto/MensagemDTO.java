package thiagosbarros.com.conversazap.interfaces.dto;

import java.time.LocalDateTime;

public class MensagemDTO {

    private String origem;
    private String texto;
    private LocalDateTime data;
    private Long idUsuario;
    private String nomeAtendente;
    private boolean lida;

    public MensagemDTO(String origem, String texto, LocalDateTime data, Long idUsuario, String nomeAtendente, boolean lida) {
        this.origem = origem;
        this.texto = texto;
        this.data = data;
        this.idUsuario = idUsuario;
        this.nomeAtendente = nomeAtendente;
        this.lida = lida;
    }

    public String getOrigem() {
        return origem;
    }

    public String getTexto() {
        return texto;
    }

    public LocalDateTime getData() {
        return data;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNomeAtendente() {
        return nomeAtendente;
    }

    public boolean isLida() {
        return lida;
    }
}