package thiagosbarros.com.conversazap.infrastructure.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thiagosbarros.com.conversazap.application.service.ClienteService;
import thiagosbarros.com.conversazap.interfaces.dto.ClienteDTO;
import thiagosbarros.com.conversazap.interfaces.dto.response.ClienteResponseDto;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDto>> listarClientes(){
        return ResponseEntity.ok().body(clienteService.listarClientes());

    }

    @PostMapping
    public ResponseEntity<ClienteResponseDto> salvar(@RequestBody ClienteDTO clienteDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.salvar(clienteDTO));
    }

    @GetMapping("{id}")
    public ResponseEntity<ClienteResponseDto> obterClientePorId(@PathVariable Long id) {
        return ResponseEntity.ok().body(clienteService.obterClientePorId(id));
    }

    @PutMapping("{id}")
    public ResponseEntity<ClienteResponseDto> atualizar(@PathVariable Long id, @RequestBody ClienteDTO clienteDTO) {
        return ResponseEntity.ok().body(clienteService.atualizarCliente(id,clienteDTO));
    }
}
