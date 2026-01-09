package thiagosbarros.com.conversazap.application.usecase;

import org.springframework.stereotype.Component;
import thiagosbarros.com.conversazap.application.service.GestaoBotService;
import thiagosbarros.com.conversazap.domain.model.Empresa;
import thiagosbarros.com.conversazap.infrastructure.security.SecurityService;
import thiagosbarros.com.conversazap.interfaces.dto.EtapaBotDTO;

import java.util.List;

@Component
public class ConfigurarBotUseCase {

    private final GestaoBotService gestaoBotService;
    private final SecurityService securityService;

    public ConfigurarBotUseCase(GestaoBotService gestaoBotService, SecurityService securityService) {
        this.gestaoBotService = gestaoBotService;
        this.securityService = securityService;
    }

    public List<EtapaBotDTO> listar(){
        Empresa empresa = securityService.usuarioLogado().getEmpresa();
        return gestaoBotService.listarPorEmpresa(empresa);
    }

    public EtapaBotDTO salvar(EtapaBotDTO dto){
        Empresa empresa = securityService.usuarioLogado().getEmpresa();
        return gestaoBotService.salvarEtapa(empresa, dto);
    }
}
