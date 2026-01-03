package thiagosbarros.com.conversazap.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thiagosbarros.com.conversazap.domain.model.Conversa;
import thiagosbarros.com.conversazap.domain.model.Mensagem;

import java.util.List;

public interface MensagemRepository extends JpaRepository<Mensagem, Long> {
    List<Mensagem> findByConversaOrderByDataAsc(Conversa conversa);
}
