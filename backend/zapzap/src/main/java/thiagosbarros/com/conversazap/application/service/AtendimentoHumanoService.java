package thiagosbarros.com.conversazap.application.service;


import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import thiagosbarros.com.conversazap.application.exception.BusinessException;
import thiagosbarros.com.conversazap.application.exception.ConversaNaoEncontradaException;
import thiagosbarros.com.conversazap.domain.enums.OrigemMensagem;
import thiagosbarros.com.conversazap.domain.enums.StatusConversa;
import thiagosbarros.com.conversazap.domain.model.Conversa;
import thiagosbarros.com.conversazap.domain.model.Empresa;
import thiagosbarros.com.conversazap.domain.model.Mensagem;
import thiagosbarros.com.conversazap.domain.model.Usuario;
import thiagosbarros.com.conversazap.domain.repository.ConversaRepository;
import thiagosbarros.com.conversazap.domain.repository.MensagemRepository;
import thiagosbarros.com.conversazap.domain.repository.UsuarioRepository;
import thiagosbarros.com.conversazap.infrastructure.security.SecurityService;
import thiagosbarros.com.conversazap.interfaces.dto.ConversaResumoDTO;
import thiagosbarros.com.conversazap.interfaces.dto.MensagemDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AtendimentoHumanoService {

    private final ConversaRepository conversaRepository;
    private final MensagemRepository mensagemRepository;
    private final SecurityService securityService;
    private final UsuarioRepository usuarioRepository;

    public AtendimentoHumanoService(
            ConversaRepository conversaRepository,
            MensagemRepository mensagemRepository,
            SecurityService securityService,
            UsuarioRepository usuarioRepository) {
        this.conversaRepository = conversaRepository;
        this.mensagemRepository = mensagemRepository;
        this.securityService = securityService;
        this.usuarioRepository = usuarioRepository;
    }


    @Transactional(readOnly = true)
    public List<ConversaResumoDTO> listarConversasAbertas(){

        Usuario usuarioLogado = securityService.usuarioLogado();
        Empresa empresa = usuarioLogado.getEmpresa();

        return conversaRepository.findByCliente_EmpresaAndStatusNot(empresa, StatusConversa.ENCERRADA)
                .stream().map(c -> new ConversaResumoDTO(
                                c.getId(),
                                c.getCliente().getTelefone(),
                                c.getCliente().getNome(),
                                c.getStatus().name(),
                                c.getUsuarioAtual() != null ? c.getUsuarioAtual().getId() : null,
                                c.getCliente().getId()

                )).collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public List<MensagemDTO> buscarMensagens(Long conversaId){
        Conversa conversa = conversaRepository.findById(conversaId)
                .orElseThrow(()-> new RuntimeException("Conversa não encontrada."));

        return mensagemRepository.findByConversaOrderByDataAsc(conversa)
                .stream().map(
                        m-> new MensagemDTO(
                        m.getOrigem().name(),
                        m.getTexto(),
                        m.getData(),
                        m.getUsuario() != null ? m.getUsuario().getId() : null,
                        m.getUsuario() != null ? m.getUsuario().getLogin() : null

                ))
                .toList();
    }
    @Transactional
    public void responder(Long conversaId, String texto) {
        Usuario usuarioLogado = securityService.usuarioLogado();
        Conversa conversa = conversaRepository.findById(conversaId)
                .orElseThrow(() -> new RuntimeException("Conversa não encontrada"));

        if (conversa.getUsuarioAtual() == null) {
            conversa.assumirConversa(usuarioLogado);
        }

        if(!usuarioLogado.equals(conversa.getUsuarioAtual())){
            throw new BusinessException("Usuário não é o atendente atual da conversa");
        }

        mensagemRepository.save(
                new Mensagem(conversa, OrigemMensagem.HUMANO, texto,usuarioLogado)
        );
    }
    @Transactional
    public void encerrar(Long conversaId) {
        Conversa conversa = conversaRepository.findById(conversaId)
                .orElseThrow(() -> new RuntimeException("Conversa não encontrada"));

        conversa.encerrar();
        conversaRepository.save(conversa);
    }

    @Transactional
    public void transferirConversa(Long conversaId, Long usuarioDestinoId) {
        Usuario usuarioLogado = securityService.usuarioLogado();

        Conversa conversa = conversaRepository.findById(conversaId)
                .orElseThrow(()-> new ConversaNaoEncontradaException("Conversa não encontrada"));

        if (conversa.getUsuarioAtual() == null || !usuarioLogado.equals(conversa.getUsuarioAtual())) {
            throw new BusinessException("Usuário não é atendente atual da conversa");
        }

        Usuario usuarioDestino = usuarioRepository.findById(usuarioDestinoId)
                .orElseThrow(()-> new BusinessException("Usuário não encontrado"));

        if(!usuarioDestino.getEmpresa().equals(usuarioLogado.getEmpresa())){
            throw new BusinessException("Você só pode transferir para atendentes da mesma empresa");
        }

        if(usuarioLogado.equals(usuarioDestino)) {
            throw new BusinessException("Usuario já está com a conversa");
        }

        conversa.assumirConversa(usuarioDestino);
        conversaRepository.save(conversa);
    }
}
