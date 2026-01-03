package thiagosbarros.com.conversazap.interfaces.dto;

public class LoginRequest {
    private String login;
    private String senha;

    public LoginRequest() {
    }

    public LoginRequest(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }
    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }
}
