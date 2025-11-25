package com.api.pagamentos.controller;

import com.api.pagamentos.dto.PagamentoRequest;
import com.api.pagamentos.dto.PagamentoResponse;
import com.api.pagamentos.service.PagamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @PostMapping
    public ResponseEntity<PagamentoResponse> create(@Valid @RequestBody PagamentoRequest request) {
        PagamentoResponse resp = pagamentoService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<PagamentoResponse> refund(@PathVariable String id) {
        PagamentoResponse resp = pagamentoService.refund(id);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<List<PagamentoResponse>> listAll() {
        List<PagamentoResponse> list = pagamentoService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoResponse> getById(@PathVariable String id) {
        PagamentoResponse resp = pagamentoService.findById(id);
        return ResponseEntity.ok(resp);
    }
}
