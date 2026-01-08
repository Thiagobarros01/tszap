package thiagosbarros.com.conversazap.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thiagosbarros.com.conversazap.domain.enums.Departamento;
import thiagosbarros.com.conversazap.domain.enums.StatusConversa;
import thiagosbarros.com.conversazap.domain.model.Cliente;
import thiagosbarros.com.conversazap.domain.model.Conversa;
import thiagosbarros.com.conversazap.domain.model.Empresa;
import thiagosbarros.com.conversazap.interfaces.dto.ConversaResumoDTO;

import java.util.List;
import java.util.Optional;

public interface ConversaRepository extends JpaRepository<Conversa, Long> {

    Optional<Conversa> findByClienteAndStatusIn(
            Cliente cliente,
            Iterable<StatusConversa> status
    );

    List<Conversa> findByCliente_EmpresaAndStatusNotAndDepartamento(
            Empresa empresa,
            StatusConversa status,
            Departamento departamento
    );

    List<Conversa> findByCliente_EmpresaAndStatusNot(Empresa clienteEmpresa, StatusConversa status);

    long countByCliente_Empresa(Empresa empresa);

    long countByCliente_EmpresaAndStatus(Empresa empresa, StatusConversa status);
}
