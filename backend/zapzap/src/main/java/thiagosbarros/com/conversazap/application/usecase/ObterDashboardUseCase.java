package thiagosbarros.com.conversazap.application.usecase;

import org.springframework.stereotype.Component;
import thiagosbarros.com.conversazap.application.service.AtendimentoService;
import thiagosbarros.com.conversazap.domain.model.Usuario;
import thiagosbarros.com.conversazap.infrastructure.security.SecurityService;
import thiagosbarros.com.conversazap.interfaces.dto.DashboardDTO;

@Component
public class ObterDashboardUseCase {
    private final AtendimentoService  atendimentoService;
    private final SecurityService securityService;

    public ObterDashboardUseCase(AtendimentoService atendimentoService, SecurityService securityService) {
        this.atendimentoService = atendimentoService;
        this.securityService = securityService;
    }

    public DashboardDTO executar(){

        Usuario usuarioLogado = securityService.usuarioLogado();

        return atendimentoService.gerarDashboard(usuarioLogado.getEmpresa());
    }
}
