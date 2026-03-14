package com.volter.backend.cashRegister;

import com.volter.backend.exceptions.ResourceNotFoundException;
import com.volter.backend.staff.Staff;
import com.volter.backend.staff.StaffService;
import com.volter.backend.transaction.Transaction;
import com.volter.backend.transaction.enums.TransactionType;
import com.volter.backend.util.Validate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashRegisterService {

    private final CashRegisterRepository cashRegisterRepository;
    private final Validate validate;
    private final StaffService staffService;

    public CashRegister getById(Long id) {
        return cashRegisterRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Cash register with ID " + id + " not found")
        );
    }

    public CashRegister save(CashRegister cashRegister) {
        return cashRegisterRepository.save(cashRegister);
    }

    public CashRegister get(Long id) {
        return cashRegisterRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Cash register with ID " + id + " not found")
        );
    }

    public CashRegister deposit(Long id, Integer depositAmount, String transactionDescription, Authentication authentication) {
        Staff staff = staffService.getById(validate.extractStaffId(authentication));
        CashRegister cashRegister = cashRegisterRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Cash register with ID " + id + " not found"));

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.CASH_DEPOSIT)
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

    public CashRegister withdraw(Long id, Integer withdrawAmount, String transactionDescription, Authentication authentication) {
        Staff staff = staffService.getById(validate.extractStaffId(authentication));
        CashRegister cashRegister = cashRegisterRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Cash register with ID " + id + " not found"));

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.CASH_WITHDRAW)
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
