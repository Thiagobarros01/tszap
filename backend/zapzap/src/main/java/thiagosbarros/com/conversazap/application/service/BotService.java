package thiagosbarros.com.conversazap.application.service;

import org.springframework.stereotype.Service;
import thiagosbarros.com.conversazap.domain.enums.Departamento;
import thiagosbarros.com.conversazap.domain.enums.StatusConversa;
import thiagosbarros.com.conversazap.domain.model.Cliente;
import thiagosbarros.com.conversazap.domain.model.Conversa;
import thiagosbarros.com.conversazap.domain.model.EtapaBot;
import thiagosbarros.com.conversazap.domain.model.OpcaoBot;
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

        return processarNavegacao(conversa, mensagemCliente.trim());
    }

    private String processarNavegacao(Conversa conversa, String textoDigitado) {
        EtapaBot etapaAtual = conversa.getEtapaBotAtual();

        Optional<OpcaoBot> opcaoEncontrada = opcaoBotRepository
                .findByEtapaOrigemAndGatilho(etapaAtual, textoDigitado);

        if (opcaoEncontrada.isEmpty()) {
            return "Opção inválida. Por favor, digite uma das opções do menu anterior.";
        }

        OpcaoBot opcao = opcaoEncontrada.get();

        if(opcao.getProximaEtapa() != null){
            conversa.setEtapaBotAtual(opcao.getProximaEtapa());
            conversaRepository.save(conversa);
            return opcao.getProximaEtapa().getMensagem();
        }

        if(opcao.getDepartamentoDestino() != null){
            conversa.setEtapaBotAtual(null);
            conversa.transferirParaHumano();
            conversa.definirDepartamento(opcao.getDepartamentoDestino());
            conversaRepository.save(conversa);

            return "Entendido! Estou transferindo você para o setor " + opcao.getDepartamentoDestino() + ". Aguarde um momento.";
        }

        return "Erro de configuração: Opção sem destino.";
    }

    private String iniciarFluxo(Conversa conversa) {
        Optional<EtapaBot> etapaInicial = etapaBotRepository
                .findByEmpresaAndInicialTrue(conversa.getCliente().getEmpresa());

        if(etapaInicial.isEmpty()){
            return "Olá! Bem-vindo. No momento não temos um menu configurado. Aguarde um atendente.";
        }

        conversa.setEtapaBotAtual(etapaInicial.get());
        conversaRepository.save(conversa);

        return etapaInicial.get().getMensagem();

    }


}
