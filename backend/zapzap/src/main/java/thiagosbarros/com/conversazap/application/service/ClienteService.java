package thiagosbarros.com.conversazap.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thiagosbarros.com.conversazap.application.exception.ClienteNaoEncontradoException;
import thiagosbarros.com.conversazap.domain.model.Cliente;
import thiagosbarros.com.conversazap.domain.model.Usuario;
import thiagosbarros.com.conversazap.domain.repository.ClienteRepository;
import thiagosbarros.com.conversazap.infrastructure.security.SecurityService;
import thiagosbarros.com.conversazap.interfaces.dto.ClienteDTO;
import thiagosbarros.com.conversazap.interfaces.dto.ClienteResponseDto;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final SecurityService securityService;

    public ClienteService(ClienteRepository clienteRepository,  SecurityService securityService) {
        this.clienteRepository = clienteRepository;
        this.securityService = securityService;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDto> listarClientes() {
        Usuario usuarioLogado = securityService.usuarioLogado();

        List<Cliente> clientes = clienteRepository.findByEmpresa(usuarioLogado.getEmpresa());
        return clientes.stream().map(c->
                new ClienteResponseDto(c.getId(),c.getNome(),c.getTelefone())).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponseDto obterClientePorId(Long id) {
        return clienteRepository.findById(id).map(c->
                new ClienteResponseDto(
                        c.getId(),
                        c.getNome(),
                        c.getTelefone()))
                .orElseThrow(()-> new ClienteNaoEncontradoException("Cliente não encontrado."));
    }

    @Transactional
    public ClienteResponseDto salvar(ClienteDTO clienteDTO) {
        Usuario usuarioLogado = securityService.usuarioLogado();
        Cliente cliente = new Cliente(
                clienteDTO.nome(),
                clienteDTO.telefone(),
                usuarioLogado.getEmpresa());
        Cliente clienteSalvo = clienteRepository.save(cliente);
        return toDto(clienteSalvo);
    }


    private ClienteResponseDto toDto(Cliente cliente) {
        return new ClienteResponseDto(cliente.getId(),cliente.getNome(),cliente.getTelefone());
    }

    @Transactional
    public ClienteResponseDto atualizarCliente(Long id, ClienteDTO clienteDTO) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(()-> new ClienteNaoEncontradoException("Cliente não encontrado."));

        cliente.definirNome(clienteDTO.nome());
        cliente.atualizarTelefone(clienteDTO.telefone());
        return toDto(cliente);

    }

}
