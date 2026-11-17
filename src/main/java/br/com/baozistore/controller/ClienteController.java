package br.com.baozistore.controller;
import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import br.com.baozistore.model.Cliente;
import br.com.baozistore.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService service;
    public ClienteController(ClienteService service) { this.service = service; }
    @GetMapping public List<Cliente> listar() { return service.listar(); }
    @GetMapping("/{id}") public Cliente buscar(@PathVariable("id") Long id) { return service.buscar(id); }
    @PostMapping public ResponseEntity<Cliente> criar(@Valid @RequestBody Cliente item) {
        Cliente salvo = service.criar(item);
        return ResponseEntity.created(URI.create("/clientes/" + salvo.getId())).body(salvo);
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable("id") Long id) { service.excluir(id); return ResponseEntity.noContent().build(); }
}
