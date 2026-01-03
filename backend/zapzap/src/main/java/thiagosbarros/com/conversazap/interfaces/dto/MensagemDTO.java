package thiagosbarros.com.conversazap.interfaces.dto;

import java.time.LocalDateTime;

public class MensagemDTO {

    private String origem;
    private String texto;
    private LocalDateTime data;
    private Long idUsuario;
    private String nomeAtendente;

    public MensagemDTO(String origem, String texto, LocalDateTime data, Long idUsuario, String nomeAtendente) {
        this.origem = origem;
        this.texto = texto;
        this.data = data;
        this.idUsuario = idUsuario;
        this.nomeAtendente = nomeAtendente;

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

    private Long getIdUsuario() {
        return idUsuario;
    }

    private String getNomeAtendente() {
        return nomeAtendente;
    }
}