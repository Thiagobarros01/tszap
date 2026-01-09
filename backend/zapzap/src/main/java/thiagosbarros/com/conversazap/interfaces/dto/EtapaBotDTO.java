package thiagosbarros.com.conversazap.interfaces.dto;


import java.util.List;

public class EtapaBotDTO {

    private Long id;
    private String mensagem;
    private boolean inicial;
    private List<OpcaoBotDTO> opcoes;

    public EtapaBotDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    public boolean isInicial() { return inicial; }
    public void setInicial(boolean inicial) { this.inicial = inicial; }
    public List<OpcaoBotDTO> getOpcoes() { return opcoes; }
    public void setOpcoes(List<OpcaoBotDTO> opcoes) { this.opcoes = opcoes; }
}