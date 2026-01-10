package thiagosbarros.com.conversazap.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thiagosbarros.com.conversazap.application.exception.BusinessException;
import thiagosbarros.com.conversazap.domain.model.Empresa;
import thiagosbarros.com.conversazap.domain.model.EtapaBot;
import thiagosbarros.com.conversazap.domain.model.OpcaoBot;
import thiagosbarros.com.conversazap.domain.repository.EtapaBotRepository;
import thiagosbarros.com.conversazap.domain.repository.OpcaoBotRepository;
import thiagosbarros.com.conversazap.interfaces.dto.EtapaBotDTO;
import thiagosbarros.com.conversazap.interfaces.dto.OpcaoBotDTO;

import java.util.ArrayList;
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
    public EtapaBotDTO salvarEtapa(Empresa empresa, EtapaBotDTO dto) {
        if (dto.getId() != null) {
            return atualizarEtapa(dto.getId(), empresa, dto);
        }

        EtapaBot etapa = new EtapaBot();
        etapa.setEmpresa(empresa);
        etapa.setMensagem(dto.getMensagem());

        if (dto.isInicial()) {
            desmarcarOutrasIniciais(empresa);
        }
        etapa.setInicial(dto.isInicial());

        if (dto.getOpcoes() != null) {
            etapa.setOpcoes(new ArrayList<>());
            mapearOpcoes(etapa, dto.getOpcoes());
        }

        EtapaBot etapaSalva = etapaBotRepository.save(etapa);
        return toDto(etapaSalva);
    }

    @Transactional
    public EtapaBotDTO atualizarEtapa(Long id, Empresa empresa, EtapaBotDTO dto) {
        EtapaBot etapa = etapaBotRepository.findById(id)
                .filter(e -> e.getEmpresa().getId().equals(empresa.getId()))
                .orElseThrow(() -> new BusinessException("Etapa não encontrada ou acesso negado"));

        etapa.setMensagem(dto.getMensagem());

        if (dto.isInicial()) {
            desmarcarOutrasIniciais(empresa);
        }
        etapa.setInicial(dto.isInicial());

        // AQUI ESTÁ A CORREÇÃO DA DUPLICAÇÃO:
        // 1. Limpa a lista existente (o orphanRemoval=true no Model vai deletar do banco)
        if (etapa.getOpcoes() != null) {
            etapa.getOpcoes().clear();
        } else {
            etapa.setOpcoes(new ArrayList<>());
        }

        // 2. Adiciona as novas (mapeadas)
        if (dto.getOpcoes() != null) {
            mapearOpcoes(etapa, dto.getOpcoes());
        }

        EtapaBot etapaSalva = etapaBotRepository.save(etapa);
        return toDto(etapaSalva);
    }

    private void mapearOpcoes(EtapaBot etapaPai, List<OpcaoBotDTO> opcoesDTO) {
        for (OpcaoBotDTO opDto : opcoesDTO) {
            OpcaoBot op = new OpcaoBot();
            op.setGatilho(opDto.getGatilho());
            op.setDepartamentoDestino(opDto.getDepartamentoDestino());
            op.setEtapaOrigem(etapaPai);

            if (opDto.getProximaEtapaId() != null) {
                // Referência apenas para o ID
                EtapaBot proxima = new EtapaBot();
                proxima.setId(opDto.getProximaEtapaId());
                op.setProximaEtapa(proxima);
            }
            etapaPai.getOpcoes().add(op);
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
