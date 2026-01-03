package thiagosbarros.com.conversazap.infrastructure.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import thiagosbarros.com.conversazap.application.usecase.ProcessarMensagemUseCase;
import thiagosbarros.com.conversazap.interfaces.dto.RespostaMensagemDTO;
import thiagosbarros.com.conversazap.interfaces.dto.WebhookMensagemDTO;

import java.util.Map;

@RestController
@RequestMapping("/webhook/twilio")
public class TwilioWebhookController {

    private final ProcessarMensagemUseCase usecase;

    public TwilioWebhookController(ProcessarMensagemUseCase usecase) {
        this.usecase = usecase;
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> receberMensagem(@RequestParam Map<String, String> params) {

        String from = params.get("From");
        String to = params.get("To");
        String body = params.getOrDefault("Body", "").trim();


        WebhookMensagemDTO dto = new WebhookMensagemDTO();
        dto.setTelefoneCliente(limparTelefone(from));
        dto.setTelefoneEmpresa(limparTelefone(to));
        dto.setMensagem(body);

        RespostaMensagemDTO resposta = usecase.executar(dto);


        String mensagemSegura = resposta.getResposta()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");

        String twiml = """
                <Response>
                    <Message>%s</Message>
                </Response>
                """.formatted(mensagemSegura);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_XML)
                .body(twiml);
    }


    private String limparTelefone(String telefone) {
        return telefone != null ? telefone.replace("whatsapp:+", "") : null;
    }

}
