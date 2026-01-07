package thiagosbarros.com.conversazap.infrastructure.whatsapp;

import org.springframework.stereotype.Component;
import thiagosbarros.com.conversazap.domain.gateway.EnvioMensagemGateway;

@Component
public class WhatsAppClient implements EnvioMensagemGateway {

    @Override
    public void enviarMensagem(String telefoneDestino, String texto) {

        // Por enquanto, simulamos o envio imprimindo no console

        System.out.println("=================================");
        System.out.println("🚀 ENVIANDO WHATSAPP (SIMULAÇÃO)");
        System.out.println("📱 Para: " + telefoneDestino);
        System.out.println("💬 Texto: " + texto);
        System.out.println("=================================");

        // Futuramente, aqui entra o código do RestTemplate/Feign pra chamar a Meta

    }
}
