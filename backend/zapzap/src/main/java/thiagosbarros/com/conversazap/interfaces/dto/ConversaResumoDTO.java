package thiagosbarros.com.conversazap.interfaces.dto;

public class ConversaResumoDTO {

    private Long conversaId;
    private String telefoneCliente;
    private String status;
    private Long usuarioId;

    public ConversaResumoDTO(Long conversaId, String telefoneCliente, String status, Long usuarioId) {
        this.conversaId = conversaId;
        this.telefoneCliente = telefoneCliente;
        this.status = status;
        this.usuarioId = usuarioId;

    }

    public Long getConversaId() {
        return conversaId;
    }

    public String getTelefoneCliente() {
        return telefoneCliente;
    }

    public String getStatus() {
        return status;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }
}