package thiagosbarros.com.conversazap.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class RsaKeyConfig {
    @Bean
    public KeyPair keyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException("Erro ao gerar chaves RSA", ex);
        }
    }
    @Bean
    public RSAPublicKey rsaPublicKey(KeyPair keyPair) { return (RSAPublicKey) keyPair.getPublic(); }
    @Bean
    public RSAPrivateKey rsaPrivateKey(KeyPair keyPair) { return (RSAPrivateKey) keyPair.getPrivate(); }
}
