package thiagosbarros.com.conversazap.infrastructure.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import thiagosbarros.com.conversazap.interfaces.dto.AtualizarUsuarioDTO;
import thiagosbarros.com.conversazap.interfaces.dto.CriarUsuarioDTO;
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
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> salvarUsuario(@RequestBody CriarUsuarioDTO usuarioDto) {
        usuarioService.salvar(usuarioDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResumoDTO>> listarUsuarios(){
        return ResponseEntity.ok().body(usuarioService.listarAtendentesDaEmpresa());
    }

    @PutMapping("{id}")
    public ResponseEntity<UsuarioResumoDTO> atualizarUsuario(@PathVariable Long id,@RequestBody AtualizarUsuarioDTO usuarioDto) {
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.atualizar(id, usuarioDto));
    }

}
