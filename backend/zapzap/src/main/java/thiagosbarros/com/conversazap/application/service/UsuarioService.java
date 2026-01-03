package thiagosbarros.com.conversazap.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thiagosbarros.com.conversazap.application.dto.UsuarioDTO;
import thiagosbarros.com.conversazap.application.exception.EmpresaNaoEncontrada;
import thiagosbarros.com.conversazap.domain.model.Empresa;
import thiagosbarros.com.conversazap.domain.model.Usuario;
import thiagosbarros.com.conversazap.domain.repository.EmpresaRepository;
import thiagosbarros.com.conversazap.domain.repository.UsuarioRepository;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmpresaRepository empresaRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, EmpresaRepository empresaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.empresaRepository = empresaRepository;

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
}
