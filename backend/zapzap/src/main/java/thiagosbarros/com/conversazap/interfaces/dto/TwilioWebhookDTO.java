package thiagosbarros.com.conversazap.interfaces.dto;

public class TwilioWebhookDTO {

    private String From;
    private String To;
    private String Body;

    public String getFrom() {
        return From;
    }

    public String getTo() {
        return To;
    }

    public String getBody() {
        return Body;
    }
}
