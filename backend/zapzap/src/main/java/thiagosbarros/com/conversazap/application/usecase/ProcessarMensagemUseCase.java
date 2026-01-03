package thiagosbarros.com.conversazap.application.usecase;

import org.springframework.stereotype.Component;
import thiagosbarros.com.conversazap.application.service.AtendimentoService;
import thiagosbarros.com.conversazap.interfaces.RespostaMensagemDTO;
import thiagosbarros.com.conversazap.interfaces.WebhookMensagemDTO;

@Component
public class ProcessarMensagemUseCase {

    private final AtendimentoService atendimentoService;

    public ProcessarMensagemUseCase(AtendimentoService atendimentoService) {
        this.atendimentoService = atendimentoService;
    }

    public RespostaMensagemDTO executar(WebhookMensagemDTO dto) {
        String resposta = atendimentoService.processarMensagem(
                dto.getTelefoneEmpresa(),
                dto.getTelefoneCliente(),
                dto.getMensagem()
        );

        return new RespostaMensagemDTO(resposta);
    }
}