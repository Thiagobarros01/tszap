package thiagosbarros.com.conversazap.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thiagosbarros.com.conversazap.domain.model.Cliente;
import thiagosbarros.com.conversazap.domain.model.Empresa;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByTelefoneAndEmpresa(String telefone, Empresa empresa);
}
