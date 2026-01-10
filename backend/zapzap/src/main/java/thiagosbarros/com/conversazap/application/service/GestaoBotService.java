package thiagosbarros.com.conversazap.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thiagosbarros.com.conversazap.domain.model.Empresa;
import thiagosbarros.com.conversazap.domain.model.EtapaBot;
import thiagosbarros.com.conversazap.domain.model.OpcaoBot;
import thiagosbarros.com.conversazap.domain.repository.EtapaBotRepository;
import thiagosbarros.com.conversazap.domain.repository.OpcaoBotRepository;
import thiagosbarros.com.conversazap.interfaces.dto.EtapaBotDTO;
import thiagosbarros.com.conversazap.interfaces.dto.OpcaoBotDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GestaoBotService {

    private final EtapaBotRepository etapaBotRepository;
    private final OpcaoBotRepository opcaoBotRepository;

    public GestaoBotService(EtapaBotRepository etapaBotRepository,
                            OpcaoBotRepository opcaoBotRepository) {
        this.etapaBotRepository = etapaBotRepository;
        this.opcaoBotRepository = opcaoBotRepository;
    }


    @Transactional(readOnly = true)
    public List<EtapaBotDTO> listarPorEmpresa(Empresa empresa){
        List<EtapaBot> etapas = etapaBotRepository.findAll();
        return etapas
                .stream().filter(e-> e.getEmpresa().getId().equals(empresa.getId()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    @Transactional
    public EtapaBotDTO salvarEtapa(Empresa empresa,EtapaBotDTO dto){
        EtapaBot etapa = new EtapaBot();

        if(dto.getId() != null){
            etapa = etapaBotRepository.findById(dto.getId()).
                    filter(e -> e.getEmpresa().getId().equals(empresa.getId()))
                    .orElse(new EtapaBot());
        }
        etapa.setMensagem(dto.getMensagem());
        etapa.setInicial(dto.isInicial());
        etapa.setEmpresa(empresa);

        if(dto.isInicial()){
            desmarcarOutrasIniciais(empresa);
        }

        EtapaBot etapaSalva = etapaBotRepository.save(etapa);

        if(dto.getOpcoes() != null){
            atualizarOpcoes(etapaSalva, dto.getOpcoes());
        }

        return toDto(etapaSalva);

    }


    @Transactional
    public void atualizarOpcoes(EtapaBot etapaPai,  List<OpcaoBotDTO> opcoesDTO ){

        for (OpcaoBotDTO opDto : opcoesDTO) {
            OpcaoBot op =  new OpcaoBot();
            if(opDto.getId() != null){
                op = opcaoBotRepository.findById(opDto.getId())
                        .orElse(new OpcaoBot());
            }

            op.setGatilho(opDto.getGatilho());
            op.setEtapaOrigem(etapaPai);
            op.setDepartamentoDestino(opDto.getDepartamentoDestino());

            if(opDto.getProximaEtapaId() != null){
                etapaBotRepository.findById(opDto.getProximaEtapaId())
                        .ifPresent(op::setProximaEtapa);
            }

                opcaoBotRepository.save(op);
        }
    }

    private void desmarcarOutrasIniciais(Empresa empresa){
        Optional<EtapaBot> antiga = etapaBotRepository.findByEmpresaAndInicialTrue(empresa);
        if(antiga.isPresent()){
            antiga.get().setInicial(false);
            etapaBotRepository.save(antiga.get());
        }
    }

    @Transactional
    public void removerEtapa(Long id){
        List<OpcaoBot> opcoesEtapa = opcaoBotRepository.findAll().stream()
                .filter(o -> o.getProximaEtapa() != null
                        && o.getProximaEtapa().getId().equals(id)).toList();

        for (OpcaoBot op : opcoesEtapa) {
            op.setProximaEtapa(null);
            opcaoBotRepository.save(op);
        }
        etapaBotRepository.deleteById(id);
    }


    private EtapaBotDTO toDto(EtapaBot e){
        EtapaBotDTO dto = new EtapaBotDTO();
        dto.setId(e.getId());
        dto.setMensagem(e.getMensagem());
        dto.setInicial(e.isInicial());


        if(e.getOpcoes() != null){
            List<OpcaoBotDTO> opBots = e.getOpcoes().stream().map(op -> {
                OpcaoBotDTO opDtos = new OpcaoBotDTO();
                opDtos.setId(op.getId());
                opDtos.setGatilho(op.getGatilho());
                opDtos.setDepartamentoDestino(op.getDepartamentoDestino());
                if(op.getProximaEtapa() != null){
                    opDtos.setProximaEtapaId(op.getProximaEtapa().getId());
                }
                return opDtos;
            }).toList();
            dto.setOpcoes(opBots);
        }
        return dto;
    }
}
