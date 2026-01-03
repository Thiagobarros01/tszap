package thiagosbarros.com.conversazap.application.service;

import org.springframework.stereotype.Service;
import thiagosbarros.com.conversazap.domain.enums.StatusConversa;
import thiagosbarros.com.conversazap.domain.model.Cliente;
import thiagosbarros.com.conversazap.domain.model.Conversa;

@Service
public class BotService {

    public String responder(Conversa conversa, String mensagemCliente) {

        // Primeira mensagem ou conversa em BOT
        if (conversa.getStatus() != StatusConversa.BOT) {
            return null;
        }

        switch(mensagemCliente.trim()){
            case "1":
                return "🕒 Funcionamos de segunda a sexta, das 08h às 18h. Sábado: 08h às 12h.";
            case "2":
                return "📍 Avenida ABC, rua 07 QD. 117";
            case "3":
                return """
                       🔧 Nossos serviços:
                       • Venda de motos
                       • Revisão
                       • Manutenção
                       • Peças e acessórios
                       """;
            case "4":
                conversa.transferirParaHumano();
                return "👤 Perfeito! Vou te transferir para um atendente.";
            default:
                return menuInicial();
        }
    }

    private String menuInicial() {
        return """
                👋 Olá, Bem-vindo à nossa empresa
                Como posso te ajudar?
                1️⃣ Horário de funcionamento
                2️⃣ Endereço
                3️⃣ Serviços
                4️⃣ Falar com atendente
               """;
    }
}
