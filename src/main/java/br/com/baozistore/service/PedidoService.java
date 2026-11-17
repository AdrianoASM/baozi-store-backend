package br.com.baozistore.service;
import java.util.List;
import br.com.baozistore.model.*;
import br.com.baozistore.dto.PedidoRequest;
import br.com.baozistore.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.*;
@Service
@Transactional(readOnly = true)
public class PedidoService {
    private final PedidoRepository pedidos;
    private final ClienteRepository clientes;
    private final ProdutoRepository produtos;
    public PedidoService(PedidoRepository pedidos, ClienteRepository clientes, ProdutoRepository produtos) {
        this.pedidos = pedidos; this.clientes = clientes; this.produtos = produtos;
    }
    public List<Pedido> listar() { return pedidos.findAll(); }
    public Pedido buscar(Long id) { return pedidos.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Pedido não encontrado")); }
    @Transactional
    public Pedido criar(PedidoRequest dados) {
        Cliente cliente = clientes.findById(dados.clienteId()).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Cliente não encontrado"));
        Produto produto = produtos.findById(dados.produtoId()).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Produto não encontrado"));
        if (!Boolean.TRUE.equals(produto.getEstoque())) throw new ResponseStatusException(CONFLICT, "Produto indisponível em estoque");
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente); pedido.setProduto(produto); pedido.setQuantidade(dados.quantidade());
        return pedidos.save(pedido);
    }
    @Transactional public void excluir(Long id) { pedidos.delete(buscar(id)); }
}
