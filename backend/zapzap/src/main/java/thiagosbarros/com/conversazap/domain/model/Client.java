package thiagosbarros.com.conversazap.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clientId;

    private String clientSecret;

    private String redirectUri;

    private String scope;

    public Client(Long id, String clientId, String clientSecret, String redirectUri, String scope) {
        this.id = id;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.scope = scope;
    }

    public Client() {

    }

    public Long getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getScope() {
        return scope;
    }

    public void atualizarSecret(String newClientSecret) {
        this.clientSecret = newClientSecret;
    }
}
