package thiagosbarros.com.conversazap.interfaces.dto;

import thiagosbarros.com.conversazap.application.command.ProcessarMensagemCommand;

public record MensagemEntradaDTO(
        String telefoneCliente,
        String telefoneEmpresa,
        String texto
) {
    public ProcessarMensagemCommand toCommand() {
        return new ProcessarMensagemCommand(
                this.telefoneEmpresa,
                this.telefoneCliente,
                this.texto
        );
    }
}
