package thiagosbarros.com.conversazap.domain.gateway;

public interface EnvioMensagemGateway {
    void enviarMensagem(String telefoneDestino, String texto);
}
