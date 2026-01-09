package thiagosbarros.com.conversazap.interfaces.dto;

import thiagosbarros.com.conversazap.domain.enums.Departamento;

public class OpcaoBotDTO {
    private Long id;
    private String gatilho;
    private Long proximaEtapaId;
    private Departamento departamentoDestino;

    public OpcaoBotDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getGatilho() { return gatilho; }
    public void setGatilho(String gatilho) { this.gatilho = gatilho; }
    public Long getProximaEtapaId() { return proximaEtapaId; }
    public void setProximaEtapaId(Long proximaEtapaId) { this.proximaEtapaId = proximaEtapaId; }
    public Departamento getDepartamentoDestino() { return departamentoDestino; }
    public void setDepartamentoDestino(Departamento departamentoDestino) {
        this.departamentoDestino = departamentoDestino;
    }
}