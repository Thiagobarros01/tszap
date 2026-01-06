package thiagosbarros.com.conversazap.application.service;

import org.springframework.stereotype.Service;
import thiagosbarros.com.conversazap.domain.enums.Departamento;
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

        switch (mensagemCliente.trim()){
            case "1":
                return transferirParaDepartamento(conversa,Departamento.COMERCIAL);
            case "2":
                return transferirParaDepartamento(conversa,Departamento.SUPORTE);
            case "3":
                return transferirParaDepartamento(conversa,Departamento.FINANCEIRO);
            case "4":
                return transferirParaDepartamento(conversa,Departamento.LOGISTICA);
            default:
                return menuInicial();
        }

    }


    private String transferirParaDepartamento(Conversa conversa, Departamento departamento) {
        conversa.definirDepartamento(departamento);
        conversa.transferirParaHumano();
        return "✅ Entendido! Transferindo para o time do " + departamento.name() + ". Aguarde um momento.";
    }

    private String menuInicial() {
        return """
                👋 Olá! Bem-vindo à T.S Zap.
                Para direcionar seu atendimento, escolha uma opção:
               
                1️⃣ Comercial / Vendas
                2️⃣ Suporte Técnico
                3️⃣ Financeiro
                4️⃣ Logistica
               
                Digite apenas o número.
               """;
    }
}
