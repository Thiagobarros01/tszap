package thiagosbarros.com.conversazap.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thiagosbarros.com.conversazap.domain.model.EtapaBot;
import thiagosbarros.com.conversazap.domain.model.OpcaoBot;

import java.util.Optional;

public interface OpcaoBotRepository extends JpaRepository<OpcaoBot, Long> {

    Optional<OpcaoBot> findByEtapaOrigemAndGatilho(EtapaBot etapaOrigem, String gatilho);
}
