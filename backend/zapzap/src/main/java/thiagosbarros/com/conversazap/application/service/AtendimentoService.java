package thiagosbarros.com.conversazap.application.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import thiagosbarros.com.conversazap.application.exception.EmpresaNaoEncontradaException;
import thiagosbarros.com.conversazap.domain.enums.OrigemMensagem;
import thiagosbarros.com.conversazap.domain.enums.StatusConversa;
import thiagosbarros.com.conversazap.domain.gateway.EnvioMensagemGateway;
import thiagosbarros.com.conversazap.domain.model.*;
import thiagosbarros.com.conversazap.domain.repository.*;
import org.springframework.stereotype.Service;
import thiagosbarros.com.conversazap.interfaces.dto.DashboardDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AtendimentoService {

    private final EmpresaRepository empresaRepository;
    private final ClienteRepository clienteRepository;
    private final ConversaRepository conversaRepository;
    private final MensagemRepository mensagemRepository;
    private final BotService botService;
    private final HorarioAtendimentoService horarioAtendimentoService;
    private final MensagemAutomaticaService mensagemAutomaticaService;
    private final EnvioMensagemGateway  envioMensagemGateway;
    private final SimpMessagingTemplate messagingTemplate;

    public AtendimentoService(
            EmpresaRepository empresaRepository,
            ClienteRepository clienteRepository,
            ConversaRepository conversaRepository,
            MensagemRepository mensagemRepository,
            BotService botService,
            HorarioAtendimentoService horarioAtendimentoService,
            MensagemAutomaticaService mensagemAutomaticaService,
            EnvioMensagemGateway envioMensagemGateway,
            SimpMessagingTemplate messagingTemplate
            ) {
        this.empresaRepository = empresaRepository;
        this.clienteRepository = clienteRepository;
        this.conversaRepository = conversaRepository;
        this.mensagemRepository = mensagemRepository;
        this.botService = botService;
        this.horarioAtendimentoService = horarioAtendimentoService;
        this.mensagemAutomaticaService = mensagemAutomaticaService;
        this.envioMensagemGateway = envioMensagemGateway;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public String processarMensagem(String telefoneEmpresa, String telefoneCliente, String texto) {

        Empresa empresa = empresaRepository.findByTelefoneWhatsApp(telefoneEmpresa)
                .orElseThrow(() -> new EmpresaNaoEncontradaException("Empresa não encontrada"));

        Cliente cliente = clienteRepository
                .findByTelefoneAndEmpresa(telefoneCliente, empresa)
                .orElseGet(() -> clienteRepository.save(new Cliente(telefoneCliente, empresa)));

        Optional<Conversa> conversaOpt = conversaRepository
                .findByClienteAndStatusIn(cliente,
                        List.of(StatusConversa.BOT, StatusConversa.HUMANO));


        Conversa conversa = conversaOpt.orElseGet(() ->
                conversaRepository.save(new Conversa(cliente)));

         Mensagem mensagemCliente =  new Mensagem(conversa, OrigemMensagem.CLIENTE, texto);
         mensagemRepository.save(mensagemCliente);
         notificarFrontend(conversa);

        if(!horarioAtendimentoService.estaDentroDoHorario(LocalDateTime.now())){
            String resposta = mensagemAutomaticaService.foraDoHorario();
            mensagemRepository.save(new Mensagem(conversa,OrigemMensagem.BOT, resposta));
            return resposta;
        }

        String respostaBot = botService.processar(conversa  ,texto);

        if (respostaBot != null) {
            mensagemRepository.save(new Mensagem(conversa, OrigemMensagem.BOT, respostaBot));

            envioMensagemGateway.enviarMensagem(telefoneCliente,respostaBot);
            notificarFrontend(conversa);

            return respostaBot;
        }

        conversa.transferirParaHumano();
        conversaRepository.save(conversa);

        return "👤 Um atendente irá te responder em breve.";
    }

    private void notificarFrontend(Conversa conversa) {
        // Vamos mandar um sinal para o tópico específico dessa conversa
        // Quem estiver "ouvindo" o tópico "/topic/conversas/{id}" vai receber.
        // Enviamos o ID da conversa apenas para avisar "tem coisa nova, atualiza aí".
        messagingTemplate.convertAndSend("/topic/conversa/" + conversa.getId(), "Nova mensagem");

        // Também avisamos o painel geral para atualizar a lista lateral (bolinha verde, última msg)
        messagingTemplate.convertAndSend("/topic/painel", "Atualizar Lista");

    }

    @Transactional(readOnly = true)
    public DashboardDTO gerarDashboard(Empresa empresa){

        long total = conversaRepository.countByCliente_Empresa(empresa);
        long humanos = conversaRepository.countByCliente_EmpresaAndStatus(empresa, StatusConversa.HUMANO);
        long bot = conversaRepository.countByCliente_EmpresaAndStatus(empresa, StatusConversa.BOT);

        return new DashboardDTO(total, humanos, bot);
    }
}