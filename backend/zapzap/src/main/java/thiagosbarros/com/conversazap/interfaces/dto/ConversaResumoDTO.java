package thiagosbarros.com.conversazap.interfaces.dto;

public class ConversaResumoDTO {

    private Long conversaId;
    private String telefoneCliente;
    private String nomeCliente;
    private String status;
    private Long usuarioId;
    private Long clienteId;

    public ConversaResumoDTO(Long conversaId, String telefoneCliente, String nomeCliente, String status, Long usuarioId, Long clienteId) {
        this.conversaId = conversaId;
        this.telefoneCliente = telefoneCliente;
        this.nomeCliente = nomeCliente;
        this.status = status;
        this.usuarioId = usuarioId;
        this.clienteId = clienteId;
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

    public Long getClienteId() {
        return clienteId;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }
}