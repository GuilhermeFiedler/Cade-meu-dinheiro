package br.cademeudinheiro.controller;

import br.cademeudinheiro.dto.GastoRequest;
import br.cademeudinheiro.dto.GastosDoMes;
import br.cademeudinheiro.dto.MensagemResponse;
import br.cademeudinheiro.model.Gasto;
import br.cademeudinheiro.service.GastoService;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gastos")
public class GastoController {
    private final GastoService service;

    public GastoController(GastoService service) {
        this.service = service;
    }

    /** US03 / RF05, RF10 – gastos do mês, do mais recente ao mais antigo. */
    @GetMapping
    public GastosDoMes listar(@RequestAttribute("uid") UUID uid, @RequestParam(required = false) String mes) {
        return service.listar(uid, mes);
    }

    /** US02 / RF03 */
    @PostMapping
    public ResponseEntity<Gasto> criar(@RequestAttribute("uid") UUID uid, @RequestBody GastoRequest r) {
        return ResponseEntity.status(201).body(service.criar(uid, r));
    }

    /** US04 / RF06 */
    @PutMapping("/{id}")
    public Gasto editar(@RequestAttribute("uid") UUID uid, @PathVariable UUID id, @RequestBody GastoRequest r) {
        return service.editar(uid, id, r);
    }

    /** US04 / RF07 – a confirmação (RN08) é feita no frontend. */
    @DeleteMapping("/{id}")
    public MensagemResponse excluir(@RequestAttribute("uid") UUID uid, @PathVariable UUID id) {
        service.excluir(uid, id);
        return new MensagemResponse("Gasto excluído");
    }
}
