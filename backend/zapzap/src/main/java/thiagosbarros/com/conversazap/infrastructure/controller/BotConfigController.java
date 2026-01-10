package thiagosbarros.com.conversazap.infrastructure.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thiagosbarros.com.conversazap.application.usecase.ConfigurarBotUseCase;
import thiagosbarros.com.conversazap.interfaces.dto.EtapaBotDTO;

import java.util.List;

@RestController
@RequestMapping("/painel/bot/config")
public class BotConfigController {

    private final ConfigurarBotUseCase  usecase;

    public BotConfigController(ConfigurarBotUseCase usecase) {
        this.usecase = usecase;
    }

    @GetMapping
    public ResponseEntity<List<EtapaBotDTO>> listarEtapas() {
        return ResponseEntity.ok(usecase.listar());
    }

    @PostMapping
    public ResponseEntity<EtapaBotDTO> salvarEtapa(@RequestBody EtapaBotDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usecase.salvar(dto));
    }

    @DeleteMapping("{idEtapa}")
    public ResponseEntity<Void> removerEtapa(@PathVariable Long idEtapa) {
        usecase.removerEtapa(idEtapa);
        return ResponseEntity.noContent().build();
    }
}
