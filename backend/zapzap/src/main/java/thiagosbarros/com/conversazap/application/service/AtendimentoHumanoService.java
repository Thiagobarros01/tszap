package thiagosbarros.com.conversazap.application.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import thiagosbarros.com.conversazap.application.exception.BusinessException;
import thiagosbarros.com.conversazap.application.exception.ConversaNaoEncontradaException;
import thiagosbarros.com.conversazap.domain.enums.OrigemMensagem;
import thiagosbarros.com.conversazap.domain.enums.Role;
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
    private final SimpMessagingTemplate messagingTemplate;

    public AtendimentoHumanoService(
            ConversaRepository conversaRepository,
            MensagemRepository mensagemRepository,
            SecurityService securityService,
            UsuarioRepository usuarioRepository,
            SimpMessagingTemplate messagingTemplate) {
        this.conversaRepository = conversaRepository;
        this.mensagemRepository = mensagemRepository;
        this.securityService = securityService;
        this.usuarioRepository = usuarioRepository;
        this.messagingTemplate = messagingTemplate;
    }



    @Transactional(readOnly = true)
    public List<ConversaResumoDTO> listarConversasAbertas(){

        Usuario usuarioLogado = securityService.usuarioLogado();
        Empresa empresa = usuarioLogado.getEmpresa();

        List<Conversa> conversas;

        if(usuarioLogado.getRole() == Role.ADMIN || usuarioLogado.getDepartamento() == null) {
            conversas = conversaRepository.findConversasRecentes(empresa, StatusConversa.ENCERRADA);
        } else {
          conversas = conversaRepository.findConversasRecentesPorDepartamento(
                    empresa,
                    StatusConversa.ENCERRADA,
                    usuarioLogado.getDepartamento());
        }
        return
                conversas.stream().map(c -> {

                   int qtdMsgNaoLidas =  (int) c.getMensagens().stream().filter(m-> !m.isLida()
                            && m.getOrigem()
                           .equals(OrigemMensagem.CLIENTE))
                           .count();

                         return new ConversaResumoDTO(
                                    c.getId(),
                                    c.getCliente().getTelefone(),
                                    c.getCliente().getNome(),
                                    c.getStatus().name(),
                                    c.getUsuarioAtual() != null ? c.getUsuarioAtual().getId() : null,
                                    c.getUsuarioAtual() != null ? c.getUsuarioAtual().getLogin() : null,
                                    c.getCliente().getId(),
                                    qtdMsgNaoLidas
                            );

                }).toList();

    }


    @Transactional(readOnly = true)
    public Page<MensagemDTO> buscarMensagens(Long conversaId, int page, int size) {
        Conversa conversa = conversaRepository.findById(conversaId)
                .orElseThrow(()-> new RuntimeException("Conversa não encontrada."));

        Pageable pageable = PageRequest.of(page, size);

        return mensagemRepository.findByConversaOrderByDataAsc(conversa,pageable)
                .map(
                        m-> new MensagemDTO(
                        m.getOrigem().name(),
                        m.getTexto(),
                        m.getData(),
                        m.getUsuario() != null ? m.getUsuario().getId() : null,
                        m.getUsuario() != null ? m.getUsuario().getLogin() : null
                ))
                ;
    }
    @Transactional
    public void responder(Long conversaId, String texto) {
        Usuario usuarioLogado = securityService.usuarioLogado();
        Conversa conversa = conversaRepository.findById(conversaId)
                .orElseThrow(() -> new RuntimeException("Conversa não encontrada"));

        if (conversa.getUsuarioAtual() == null || conversa.getStatus().equals(StatusConversa.BOT)) {
            conversa.assumirConversa(usuarioLogado);
        }

        if(!usuarioLogado.equals(conversa.getUsuarioAtual())){
            throw new BusinessException("Usuário não é o atendente atual da conversa");
        }

        mensagemRepository.save(
                new Mensagem(conversa, OrigemMensagem.HUMANO, texto,usuarioLogado)
        );

        notificarFrontend(conversa);

    }
    @Transactional
    public void encerrar(Long conversaId) {
        Usuario usuarioLogado = securityService.usuarioLogado();
        Conversa conversa = conversaRepository.findById(conversaId)
                .orElseThrow(() -> new RuntimeException("Conversa não encontrada"));

        if(!usuarioLogado.equals(conversa.getUsuarioAtual()) && usuarioLogado.getRole() != Role.ADMIN){
            throw new BusinessException("Usuário não pode encerrar o chat, não é o atendente atual da conversa");
        }
        conversa.encerrarAtendimento();
        conversaRepository.save(conversa);
        notificarFrontend(conversa);
    }

    @Transactional
    public void transferirConversa(Long conversaId, Long usuarioDestinoId) {
        Usuario usuarioLogado = securityService.usuarioLogado();

        Conversa conversa = conversaRepository.findById(conversaId)
                .orElseThrow(()-> new ConversaNaoEncontradaException("Conversa não encontrada"));

        if (conversa.getUsuarioAtual() == null && !usuarioLogado.equals(conversa.getUsuarioAtual()) && usuarioLogado.getRole() != Role.ADMIN) {
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

    public void marcarMensagensComoLida(Long idConversa) {
        Conversa conversa = conversaRepository.findById(idConversa)
                .orElseThrow(()-> new ConversaNaoEncontradaException("Conversa não encontrada"));

        conversa.getMensagens().stream().filter(m -> !m.isLida() && m.getOrigem() == OrigemMensagem.CLIENTE )
                .forEach(m -> m.marcarComoLida(true)
        );

        conversaRepository.save(conversa);
    }

    private void notificarFrontend(Conversa conversa) {
        // Vamos mandar um sinal para o tópico específico dessa conversa
        // Quem estiver "ouvindo" o tópico "/topic/conversas/{id}" vai receber.
        // Enviamos o ID da conversa apenas para avisar "tem coisa nova, atualiza aí".
        messagingTemplate.convertAndSend("/topic/conversa/" + conversa.getId(), "Nova mensagem");

        // Também avisamos o painel geral para atualizar a lista lateral (bolinha verde, última msg)
        messagingTemplate.convertAndSend("/topic/painel", "Atualizar Lista");

    }
}
