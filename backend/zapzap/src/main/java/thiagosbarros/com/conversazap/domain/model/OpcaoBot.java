package thiagosbarros.com.conversazap.domain.model;

import jakarta.persistence.*;
import thiagosbarros.com.conversazap.domain.enums.Departamento;

@Entity
@Table(name = "opcoes_bot")
public class OpcaoBot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gatilho;

    @ManyToOne
    @JoinColumn(name = "etapa_origem_id")
    private EtapaBot etapaOrigem;

    @ManyToOne
    @JoinColumn(name = "proxima_etapa_id")
    private EtapaBot proximaEtapa;

    @Enumerated(value = EnumType.STRING)
    private Departamento departamento;

    public OpcaoBot() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGatilho() {
        return gatilho;
    }

    public void setGatilho(String gatilho) {
        this.gatilho = gatilho;
    }

    public EtapaBot getEtapaOrigem() {
        return etapaOrigem;
    }

    public void setEtapaOrigem(EtapaBot etapaOrigem) {
        this.etapaOrigem = etapaOrigem;
    }

    public EtapaBot getProximaEtapa() {
        return proximaEtapa;
    }

    public void setProximaEtapa(EtapaBot proximaEtapa) {
        this.proximaEtapa = proximaEtapa;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }
}
