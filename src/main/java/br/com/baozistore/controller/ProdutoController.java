package br.com.baozistore.controller;
import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import br.com.baozistore.model.Produto;
import br.com.baozistore.service.ProdutoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/produtos")
public class ProdutoController {
    private final ProdutoService service;
    public ProdutoController(ProdutoService service) { this.service = service; }
    @GetMapping public List<Produto> listar() { return service.listar(); }
    @GetMapping("/{id}") public Produto buscar(@PathVariable("id") Long id) { return service.buscar(id); }
    @PostMapping public ResponseEntity<Produto> criar(@Valid @RequestBody Produto item) {
        Produto salvo = service.criar(item);
        return ResponseEntity.created(URI.create("/produtos/" + salvo.getId())).body(salvo);
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable("id") Long id) { service.excluir(id); return ResponseEntity.noContent().build(); }
}
