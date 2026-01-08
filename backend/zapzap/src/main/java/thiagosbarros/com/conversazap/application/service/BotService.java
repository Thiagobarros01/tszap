package thiagosbarros.com.conversazap.application.service;

import org.springframework.stereotype.Service;
import thiagosbarros.com.conversazap.domain.enums.Departamento;
import thiagosbarros.com.conversazap.domain.enums.StatusConversa;
import thiagosbarros.com.conversazap.domain.model.Cliente;
import thiagosbarros.com.conversazap.domain.model.Conversa;
import thiagosbarros.com.conversazap.domain.model.EtapaBot;
import thiagosbarros.com.conversazap.domain.repository.ConversaRepository;
import thiagosbarros.com.conversazap.domain.repository.EtapaBotRepository;
import thiagosbarros.com.conversazap.domain.repository.OpcaoBotRepository;

import java.util.Optional;

@Service
public class BotService {

    private final EtapaBotRepository  etapaBotRepository;
    private final OpcaoBotRepository opcaoBotRepository;
    private final ConversaRepository  conversaRepository;

    public BotService(EtapaBotRepository etapaBotRepository, OpcaoBotRepository opcaoBotRepository, ConversaRepository conversaRepository) {
        this.etapaBotRepository = etapaBotRepository;
        this.opcaoBotRepository = opcaoBotRepository;
        this.conversaRepository = conversaRepository;
    }


    public String processar(Conversa conversa, String mensagemCliente){
        if(conversa.getEtapaBotAtual() == null){
            return iniciarFluxo(conversa);
        }
    }

    private String iniciarFluxo(Conversa conversa) {
        Optional<EtapaBot> etapaInicial = etapaBotRepository
                .findByEmpresaAndInicialTrue(conversa.getCliente().getEmpresa());

        if(etapaInicial.isEmpty()){
            return "Olá! Bem-vindo. No momento não temos um menu configurado. Aguarde um atendente.";
        }


    }


}
