package thiagosbarros.com.conversazap.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thiagosbarros.com.conversazap.interfaces.dto.UsuarioDTO;
import thiagosbarros.com.conversazap.application.exception.EmpresaNaoEncontrada;
import thiagosbarros.com.conversazap.application.exception.UsuarioNaoEncontradoException;
import thiagosbarros.com.conversazap.domain.model.Empresa;
import thiagosbarros.com.conversazap.domain.model.Usuario;
import thiagosbarros.com.conversazap.domain.repository.EmpresaRepository;
import thiagosbarros.com.conversazap.domain.repository.UsuarioRepository;
import thiagosbarros.com.conversazap.infrastructure.security.SecurityService;
import thiagosbarros.com.conversazap.interfaces.dto.UsuarioResumoDTO;

import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmpresaRepository empresaRepository;
    private final SecurityService securityService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          EmpresaRepository empresaRepository,
                          SecurityService securityService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.empresaRepository = empresaRepository;
        this.securityService = securityService;
    }
    @Transactional
    public void salvar(UsuarioDTO dto){
        String senhaCriptografada = passwordEncoder.encode(dto.getSenha());

        Empresa empresa = empresaRepository.findById(dto.getIdEmpresa())
                .orElseThrow(() -> new EmpresaNaoEncontrada("Empresa não encontrada"));

        Usuario usuario = new Usuario(dto.getLogin(),dto.getEmail(),senhaCriptografada,dto.getRole(),empresa);
        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario obterPorLogin(String login){
        return usuarioRepository.findByLogin((login));
    }
    @Transactional(readOnly = true)
    public Usuario obterPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResumoDTO> listarAtendentesDaEmpresa() {
        Usuario usuarioLogado = securityService.usuarioLogado();


        return usuarioRepository.findByEmpresaAndAtivoTrue(usuarioLogado.getEmpresa())
                .stream()
                .filter(u -> !u.getId().equals(usuarioLogado.getId()))
                .map(u -> new UsuarioResumoDTO(u.getId(), u.getLogin(), u.getEmail(), u.getRole()))
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResumoDTO obterUsuarioPorId(Long id){
        return usuarioRepository.findById(id).map(u-> new UsuarioResumoDTO(
                        u.getId(),
                        u.getLogin(),
                        u.getEmail(),
                        u.getRole()
                ))
                .orElseThrow(()-> new UsuarioNaoEncontradoException("Usuario não encontrado!"));
    }
}
