package br.cademeudinheiro.dto;

import br.cademeudinheiro.model.Gasto;
import java.util.List;

public record GastosDoMes(String mes, List<Gasto> gastos) {}
