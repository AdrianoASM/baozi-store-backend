package br.com.baozistore.service;
import java.util.List;
import br.com.baozistore.model.Produto;
import br.com.baozistore.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.*;
@Service
@Transactional(readOnly = true)
public class ProdutoService {
    private final ProdutoRepository repository;
    private final PedidoRepository pedidos;
    public ProdutoService(ProdutoRepository repository, PedidoRepository pedidos) { this.repository = repository; this.pedidos = pedidos; }
    public List<Produto> listar() { return repository.findAll(); }
    public Produto buscar(Long id) { return repository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Produto não encontrado")); }
    @Transactional
    public Produto criar(Produto item) {
        if (item.getId() != null) throw new ResponseStatusException(BAD_REQUEST, "Não informe id na criação");
        return repository.save(item);
    }
    @Transactional
    public void excluir(Long id) {
        Produto item = buscar(id);
        if (pedidos.existsByProdutoId(id)) throw new ResponseStatusException(CONFLICT, "Há pedidos vinculados a este registro");
        repository.delete(item);
    }
}
