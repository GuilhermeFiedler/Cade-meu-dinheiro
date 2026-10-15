package br.cademeudinheiro.controller;

import br.cademeudinheiro.dto.ResumoResponse;
import br.cademeudinheiro.service.ResumoService;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resumo")
public class ResumoController {
    private final ResumoService service;

    public ResumoController(ResumoService service) {
        this.service = service;
    }

    /** US05, US06 / RF08, RF09 */
    @GetMapping
    public ResumoResponse resumo(@RequestAttribute("uid") UUID uid, @RequestParam(required = false) String mes) {
        return service.resumo(uid, mes);
    }
}
