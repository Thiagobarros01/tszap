package thiagosbarros.com.conversazap.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thiagosbarros.com.conversazap.domain.model.Empresa;
import thiagosbarros.com.conversazap.domain.model.EtapaBot;

import java.util.Optional;

public interface EtapaBotRepository extends JpaRepository<EtapaBot, Long> {

    Optional<EtapaBot> findByEmpresaAndInicialTrue(Empresa empresa);
}
