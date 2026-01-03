package thiagosbarros.com.conversazap.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thiagosbarros.com.conversazap.domain.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByClientId(String clientId);
}
