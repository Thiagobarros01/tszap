package thiagosbarros.com.conversazap.infrastructure.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thiagosbarros.com.conversazap.application.command.ProcessarMensagemCommand;
import thiagosbarros.com.conversazap.application.usecase.ProcessarMensagemUseCase;
import thiagosbarros.com.conversazap.interfaces.RespostaMensagemDTO;
import thiagosbarros.com.conversazap.interfaces.WebhookMensagemDTO;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final ProcessarMensagemUseCase useCase;

    public WebhookController(ProcessarMensagemUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<RespostaMensagemDTO> receberMensagem(
            @RequestBody WebhookMensagemDTO dto
    ) {

        ProcessarMensagemCommand command = new ProcessarMensagemCommand(
                dto.getTelefoneEmpresa(),
                dto.getTelefoneCliente(),
                dto.getMensagem()
        );

        return ResponseEntity.ok(useCase.executar(command));
    }
}