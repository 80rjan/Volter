package com.volter.backend.cashRegister.application;

import com.volter.backend.cashRegister.domain.model.CashRegister;
import com.volter.backend.cashRegister.domain.repository.CashRegisterRepository;
import com.volter.backend.common.exceptions.ResourceNotFoundException;
import com.volter.backend.staff.Staff;
import com.volter.backend.staff.StaffService;
import com.volter.backend.transaction.domain.model.enums.TransactionAction;
import com.volter.backend.cashRegister.domain.model.CashRegisterTransaction;
import com.volter.backend.common.utils.Validate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CashRegisterService {

    private final CashRegisterRepository cashRegisterRepository;
    private final Validate validate;
    private final StaffService staffService;

    @Transactional
    public CashRegister getById(Long id) {
        return cashRegisterRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Cash register with ID " + id + " not found")
        );
    }

    @Transactional
    public CashRegister save(CashRegister cashRegister) {
        return cashRegisterRepository.save(cashRegister);
    }

    @Transactional
    public CashRegister get(Long id) {
        return cashRegisterRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Cash register with ID " + id + " not found")
        );
    }

    @Transactional
    public CashRegister deposit(Long id, Integer depositAmount, String transactionDescription, Authentication authentication) {
        Staff staff = staffService.getById(validate.extractStaffId(authentication));
        CashRegister cashRegister = cashRegisterRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Cash register with ID " + id + " not found"));

        CashRegisterTransaction transaction = CashRegisterTransaction.builder()
                .action(TransactionAction.DEPOSIT)
                .cashIn(depositAmount)
                .cashOut(0)
                .profit(0)
                .description(transactionDescription)
                .staff(staff)
                .cashRegister(cashRegister)
                .build();

        cashRegister.setBalance(cashRegister.getBalance() + depositAmount);
        cashRegister.getTransactions().add(transaction);

        return cashRegisterRepository.save(cashRegister); // transaction will be saved due to cascade persist
    }

    @Transactional
    public CashRegister withdraw(Long id, Integer withdrawAmount, String transactionDescription, Authentication authentication) {
        Staff staff = staffService.getById(validate.extractStaffId(authentication));
        CashRegister cashRegister = cashRegisterRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Cash register with ID " + id + " not found"));

        CashRegisterTransaction transaction = CashRegisterTransaction.builder()
                .action(TransactionAction.WITHDRAW)
                .cashIn(0)
                .cashOut(withdrawAmount)
                .profit(0)
                .description(transactionDescription)
                .staff(staff)
                .cashRegister(cashRegister)
                .build();

        cashRegister.setBalance(cashRegister.getBalance() - withdrawAmount);
        cashRegister.getTransactions().add(transaction);

        return cashRegisterRepository.save(cashRegister); // transaction will be saved due to cascade persist
    }
}
