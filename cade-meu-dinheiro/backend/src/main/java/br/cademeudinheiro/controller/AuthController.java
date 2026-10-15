package br.cademeudinheiro.controller;

import br.cademeudinheiro.dto.LoginRequest;
import br.cademeudinheiro.dto.RegistroRequest;
import br.cademeudinheiro.dto.SessaoResponse;
import br.cademeudinheiro.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    /** US01 / RF01 */
    @PostMapping("/registrar")
    public ResponseEntity<SessaoResponse> registrar(@RequestBody RegistroRequest r) {
        return ResponseEntity.status(201).body(service.registrar(r));
    }

    /** US01 / RF02 */
    @PostMapping("/entrar")
    public SessaoResponse entrar(@RequestBody LoginRequest r) {
        return service.entrar(r);
    }
}
