package thiagosbarros.com.conversazap.infrastructure.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thiagosbarros.com.conversazap.application.service.ClientService;
import thiagosbarros.com.conversazap.domain.model.Client;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<Void> salvar(@RequestBody Client client) {
        clientService.salvarClient(client);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("{clientId}")
    public ResponseEntity<Client> buscarPorClientId(@PathVariable String clientId){
        return ResponseEntity.ok(clientService.obterPorClientId(clientId));
    }
}
