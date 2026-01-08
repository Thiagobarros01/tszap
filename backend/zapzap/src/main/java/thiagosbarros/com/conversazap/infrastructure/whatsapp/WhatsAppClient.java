package thiagosbarros.com.conversazap.infrastructure.whatsapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import thiagosbarros.com.conversazap.domain.gateway.EnvioMensagemGateway;

@Slf4j
@Component
public class WhatsAppClient implements EnvioMensagemGateway {

    @Override
    public void enviarMensagem(String telefoneDestino, String texto) {

        // Por enquanto, simulamos o envio imprimindo no console

        log.info("🚀 [SIMULAÇÃO ENVIO] Para: {} | Texto: {}", telefoneDestino, texto);

        // Futuramente, aqui entra o código do RestTemplate/Feign pra chamar a Meta

    }
}
