package thiagosbarros.com.conversazap.infrastructure.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thiagosbarros.com.conversazap.application.usecase.ProcessarMensagemUseCase;
import thiagosbarros.com.conversazap.interfaces.dto.MensagemEntradaDTO;
import thiagosbarros.com.conversazap.interfaces.dto.RespostaMensagemDTO;

@RestController
@RequestMapping("/api/v1/webhook")
public class ApiWebhookController {

    private final ProcessarMensagemUseCase useCase;

    public ApiWebhookController(ProcessarMensagemUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<RespostaMensagemDTO> receberMensagem(
            @RequestBody MensagemEntradaDTO dto
    ) {
        System.out.println("🔌 [API] Mensagem recebida de: " + dto.telefoneCliente());

        var resposta = useCase.executar(dto.toCommand());

        return ResponseEntity.ok(resposta);
    }
}