package br.com.baozistore.service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import br.com.baozistore.model.Cliente;
import br.com.baozistore.repository.ClienteRepository;
import br.com.baozistore.repository.PedidoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ClienteService {
    private final ClienteRepository repository;
    private final PedidoRepository pedidos;

    public ClienteService(ClienteRepository repository, PedidoRepository pedidos) {
        this.repository = repository;
        this.pedidos = pedidos;
    }

    public List<Cliente> listar() {
        return repository.findAll();
    }

    public Cliente buscar(Long id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(NOT_FOUND, "Cliente não encontrado"));
    }

    @Transactional
    public Cliente criar(Cliente item) {
        if (item.getId() != null)
            throw new ResponseStatusException(BAD_REQUEST, "Não informe id na criação");
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        Cliente item = buscar(id);
        if (pedidos.existsByClienteId(id))
            throw new ResponseStatusException(CONFLICT, "Há pedidos vinculados a este registro");
        repository.delete(item);
    }
}
