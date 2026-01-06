package thiagosbarros.com.conversazap.interfaces.dto;

public class ConversaResumoDTO {

    private Long conversaId;
    private String telefoneCliente;
    private String nomeCliente;
    private String status;
    private Long usuarioId;
    private String nomeAtendente;
    private Long clienteId;
    private int naoLidas;

    public ConversaResumoDTO(Long conversaId, String telefoneCliente, String nomeCliente, String status, Long usuarioId,String nomeAtendente, Long clienteId, int naoLidas) {
        this.conversaId = conversaId;
        this.telefoneCliente = telefoneCliente;
        this.nomeCliente = nomeCliente;
        this.status = status;
        this.usuarioId = usuarioId;
        this.nomeAtendente = nomeAtendente;
        this.clienteId = clienteId;
        this.naoLidas = naoLidas;

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

    public int getNaoLidas() {
        return naoLidas;
    }

    public String getNomeAtendente() {
        return nomeAtendente;
    }
}