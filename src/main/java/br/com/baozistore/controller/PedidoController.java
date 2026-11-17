package br.com.baozistore.controller;
import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import br.com.baozistore.dto.PedidoRequest;
import br.com.baozistore.model.Pedido;
import br.com.baozistore.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/pedidos")
public class PedidoController {
    private final PedidoService service;
    public PedidoController(PedidoService service) { this.service = service; }
    @GetMapping public List<Pedido> listar() { return service.listar(); }
    @GetMapping("/{id}") public Pedido buscar(@PathVariable("id") Long id) { return service.buscar(id); }
    @PostMapping public ResponseEntity<Pedido> criar(@Valid @RequestBody PedidoRequest dados) {
        Pedido salvo = service.criar(dados);
        return ResponseEntity.created(URI.create("/pedidos/" + salvo.getId())).body(salvo);
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable("id") Long id) { service.excluir(id); return ResponseEntity.noContent().build(); }
}
