package thiagosbarros.com.conversazap.application.service;

import org.springframework.stereotype.Service;

@Service
public class MensagemAutomaticaService {

    public String foraDoHorario(){
        return """
                ⏰ Nosso horário de atendimento é:
                               Segunda a sexta: 08h às 18h
                               Sábado: 08h às 12h
                
                               📩 Sua mensagem foi registrada.
                               Assim que estivermos disponíveis, um atendente responderá.
                """;
    }

}
