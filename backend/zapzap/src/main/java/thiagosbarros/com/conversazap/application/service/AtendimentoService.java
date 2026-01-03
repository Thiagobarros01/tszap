package thiagosbarros.com.conversazap.application.service;

import jakarta.transaction.Transactional;
import thiagosbarros.com.conversazap.domain.enums.OrigemMensagem;
import thiagosbarros.com.conversazap.domain.enums.StatusConversa;
import thiagosbarros.com.conversazap.domain.model.*;
import thiagosbarros.com.conversazap.domain.repository.*;
import org.springframework.stereotype.Service;
import thiagosbarros.com.conversazap.infrastructure.security.SecurityService;

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

    public AtendimentoService(
            EmpresaRepository empresaRepository,
            ClienteRepository clienteRepository,
            ConversaRepository conversaRepository,
            MensagemRepository mensagemRepository,
            BotService botService,
            HorarioAtendimentoService horarioAtendimentoService,
            MensagemAutomaticaService mensagemAutomaticaService
            ) {
        this.empresaRepository = empresaRepository;
        this.clienteRepository = clienteRepository;
        this.conversaRepository = conversaRepository;
        this.mensagemRepository = mensagemRepository;
        this.botService = botService;
        this.horarioAtendimentoService = horarioAtendimentoService;
        this.mensagemAutomaticaService = mensagemAutomaticaService;

    }

    @Transactional
    public String processarMensagem(String telefoneEmpresa, String telefoneCliente, String texto) {

        Empresa empresa = empresaRepository.findByTelefoneWhatsApp(telefoneEmpresa)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        Cliente cliente = clienteRepository
                .findByTelefoneAndEmpresa(telefoneCliente, empresa)
                .orElseGet(() -> clienteRepository.save(new Cliente(telefoneCliente, empresa)));

        Optional<Conversa> conversaOpt = conversaRepository
                .findByClienteAndStatusIn(cliente,
                        List.of(StatusConversa.BOT, StatusConversa.HUMANO));


        Conversa conversa = conversaOpt.orElseGet(() ->
                conversaRepository.save(new Conversa(cliente)));

        mensagemRepository.save(new Mensagem(conversa, OrigemMensagem.CLIENTE, texto));

        if(!horarioAtendimentoService.estaDentroDoHorario(LocalDateTime.now())){
            String resposta = mensagemAutomaticaService.foraDoHorario();
            mensagemRepository.save(new Mensagem(conversa,OrigemMensagem.BOT, resposta));
            return resposta;
        }

        String respostaBot = botService.responder(conversa  ,texto);

        if (respostaBot != null) {
            mensagemRepository.save(new Mensagem(conversa, OrigemMensagem.BOT, respostaBot));
            return respostaBot;
        }

        conversa.transferirParaHumano();
        conversaRepository.save(conversa);

        return "👤 Um atendente irá te responder em breve.";
    }
}