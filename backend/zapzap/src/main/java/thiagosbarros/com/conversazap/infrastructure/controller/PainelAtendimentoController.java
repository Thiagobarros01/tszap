package thiagosbarros.com.conversazap.infrastructure.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thiagosbarros.com.conversazap.application.service.AtendimentoHumanoService;
import thiagosbarros.com.conversazap.application.usecase.ObterDashboardUseCase;
import thiagosbarros.com.conversazap.interfaces.dto.ConversaResumoDTO;
import thiagosbarros.com.conversazap.interfaces.dto.DashboardDTO;
import thiagosbarros.com.conversazap.interfaces.dto.MensagemDTO;
import thiagosbarros.com.conversazap.interfaces.dto.ResponderMensagemDTO;

import java.util.List;

@RestController
@RequestMapping("/painel/atendimento")
public class PainelAtendimentoController {

    private final AtendimentoHumanoService service;
    private final ObterDashboardUseCase  obterDashboardUseCase;

    public PainelAtendimentoController(AtendimentoHumanoService service,
                                       ObterDashboardUseCase obterDashboardUseCase) {
        this.service = service;
        this.obterDashboardUseCase = obterDashboardUseCase;
    }

    @GetMapping("/conversas")
    public ResponseEntity<List<ConversaResumoDTO>> listarConversas() {
        return ResponseEntity.ok(service.listarConversasAbertas());
    }

    @GetMapping("/conversas/{id}/mensagens")
    public ResponseEntity<Page<MensagemDTO>> buscarMensagens(@PathVariable Long id,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "20") int size ) {
        return ResponseEntity.ok(service.buscarMensagens(id, page, size));
    }

    @PostMapping("/conversas/{id}/responder")
    public ResponseEntity<Void> responder(
            @PathVariable Long id,
            @RequestBody ResponderMensagemDTO dto
    ) {
        service.responder(id, dto.getTexto());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/conversas/{id}/encerrar")
    public ResponseEntity<Void> encerrar(@PathVariable Long id) {
        service.encerrar(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/conversas/{idConversa}/transferir/{idAtendente}")
    public ResponseEntity<Void> transferirConversa(@PathVariable Long idConversa, @PathVariable Long idAtendente) {
        service.transferirConversa(idConversa,idAtendente);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/conversas/{idConversa}/ler")
    public ResponseEntity<Void> marcarMensagensComoLida(@PathVariable Long idConversa) {
        service.marcarMensagensComoLida(idConversa);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboard() {
        DashboardDTO dashboard = obterDashboardUseCase.executar();
        return ResponseEntity.ok().body(dashboard);
    }

}