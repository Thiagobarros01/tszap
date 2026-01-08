package thiagosbarros.com.conversazap.infrastructure.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thiagosbarros.com.conversazap.application.usecase.ProcessarMensagemUseCase;
import thiagosbarros.com.conversazap.interfaces.dto.MensagemEntradaDTO;
import thiagosbarros.com.conversazap.interfaces.dto.RespostaMensagemDTO;

@Slf4j
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
            log.info("🔌 [API] Recebendo mensagem de: {}", dto.telefoneCliente());
            var resposta = useCase.executar(dto.toCommand());

            log.info("✅ Processado com sucesso.");
            return ResponseEntity.ok(resposta);


    }
}