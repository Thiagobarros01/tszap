package thiagosbarros.com.conversazap.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thiagosbarros.com.conversazap.domain.model.Client;
import thiagosbarros.com.conversazap.domain.repository.ClientRepository;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientService(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Client salvarClient(Client client) {
        String senhaCriptografada = passwordEncoder.encode(client.getClientSecret());
        client.atualizarSecret(senhaCriptografada);
        return clientRepository.save(client);
    }

    @Transactional(readOnly = true)
    public Client obterPorClientId(String clientID){
        return clientRepository.findByClientId(clientID);
    }

}
