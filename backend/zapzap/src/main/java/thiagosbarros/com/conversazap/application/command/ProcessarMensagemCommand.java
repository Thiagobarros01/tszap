package thiagosbarros.com.conversazap.application.command;

//Command é uma intenção pura
public class ProcessarMensagemCommand {

    private final String telefoneEmpresa;
    private final String telefoneCliente;
    private final String mensagem;

    public  ProcessarMensagemCommand(String telefoneEmpresa,
                                     String telefoneCliente,
                                     String mensagem) {
        this.telefoneEmpresa = telefoneEmpresa;
        this.telefoneCliente = telefoneCliente;
        this.mensagem = mensagem;
    }

    public String getTelefoneEmpresa() {
        return telefoneEmpresa;
    }
    public String getTelefoneCliente() {
        return telefoneCliente;
    }
    public String getMensagem() {
        return mensagem;
    }

}
