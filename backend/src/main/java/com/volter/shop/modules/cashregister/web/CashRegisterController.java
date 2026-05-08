package com.volter.shop.modules.cashregister.web;

import com.volter.shop.modules.cashregister.application.CashRegisterService;
import com.volter.shop.modules.cashregister.web.request.CashRegisterSessionCloseRequest;
import com.volter.shop.modules.cashregister.web.request.CashRegisterSessionDepositRequest;
import com.volter.shop.modules.cashregister.web.request.CashRegisterSessionOpenRequest;
import com.volter.shop.modules.cashregister.web.request.CashRegisterSessionWithdrawRequest;
import com.volter.shop.modules.cashregister.web.response.CashRegisterSessionResponse;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.cashregister.infrastructure.mapper.CashRegisterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/cash-register")
public class CashRegisterController {

    private final CashRegisterService cashRegisterService;
    private final CashRegisterMapper cashRegisterMapper;

    @GetMapping
    public ResponseEntity<CashRegisterSessionResponse> getCurrentSession() {
        CashRegisterSession session = cashRegisterService.getOpenSessionByStaff();
        return ResponseEntity.ok(cashRegisterMapper.toSessionResponse(session));
    }

    @PostMapping
    public ResponseEntity<CashRegisterSessionResponse> openSession(@RequestBody CashRegisterSessionOpenRequest request) {
        CashRegisterSession session = cashRegisterService.openSession(request);
        return ResponseEntity.ok(cashRegisterMapper.toSessionResponse(session));
    }

    @PutMapping("/close")
    public ResponseEntity<CashRegisterSessionResponse> closeSession(@RequestBody CashRegisterSessionCloseRequest request) {
        CashRegisterSession session = cashRegisterService.closeSession(request);
        return ResponseEntity.ok(cashRegisterMapper.toSessionResponse(session));
    }

    @PutMapping("/withdraw")
    public ResponseEntity<CashRegisterSessionResponse> withdraw(@RequestBody CashRegisterSessionWithdrawRequest request) {
        CashRegisterSession session = cashRegisterService.withdraw(request);
        return ResponseEntity.ok(cashRegisterMapper.toSessionResponse(session));
    }

    @PutMapping("/deposit")
    public ResponseEntity<CashRegisterSessionResponse> deposit(@RequestBody CashRegisterSessionDepositRequest request) {
        CashRegisterSession session = cashRegisterService.deposit(request);
        return ResponseEntity.ok(cashRegisterMapper.toSessionResponse(session));
    }
}
