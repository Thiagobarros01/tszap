package thiagosbarros.com.conversazap.interfaces.dto;

public class RespostaMensagemDTO {

    private String resposta;

    public RespostaMensagemDTO(String resposta) {
        this.resposta = resposta;
    }

    public String getResposta() {
        return resposta;
    }
}
