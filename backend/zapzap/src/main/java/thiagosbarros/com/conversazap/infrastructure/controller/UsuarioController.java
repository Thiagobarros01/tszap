package thiagosbarros.com.conversazap.infrastructure.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thiagosbarros.com.conversazap.interfaces.dto.UsuarioDTO;
import thiagosbarros.com.conversazap.application.service.UsuarioService;
import thiagosbarros.com.conversazap.interfaces.dto.UsuarioResumoDTO;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> salvarUsuario(@RequestBody UsuarioDTO usuarioDto) {
        usuarioService.salvar(usuarioDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResumoDTO>> listarUsuarios(){
        return ResponseEntity.ok().body(usuarioService.listarAtendentesDaEmpresa());
    }

}
