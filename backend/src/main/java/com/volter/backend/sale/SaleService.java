package com.volter.backend.sale;

import com.volter.backend.cashRegister.CashRegister;
import com.volter.backend.cashRegister.CashRegisterService;
import com.volter.backend.exceptions.ResourceNotFoundException;
import com.volter.backend.sale.enums.SaleStatus;
import com.volter.backend.staff.Staff;
import com.volter.backend.staff.StaffService;
import com.volter.backend.transaction.Transaction;
import com.volter.backend.transaction.enums.TransactionType;
import com.volter.backend.util.Validate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final Validate validate;
    private final StaffService staffService;
    private final SaleRepository saleRepository;
    private final CashRegisterService cashRegisterService;

    public Sale save(Sale sale) {
        return saleRepository.save(sale);
    }

    /**
     * Retrieves all sales
     */
    @Transactional
    public List<Sale> getAll() {
        return saleRepository.findAll();
    }

    /**
     * Retrieves a sale by its ID
     */
    @Transactional
    public Sale getById(Long id) {
        return saleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Sale with ID " + id + " not found")
        );
    }

    @Transactional
    public Sale sell(Long id, Integer soldPrice, String transactionDescription, Long cashRegisterId, Authentication authentication) {
        Staff staff = staffService.getById(validate.extractStaffId(authentication));
        Sale sale = getById(id);
        CashRegister cashRegister = cashRegisterService.getById(cashRegisterId);

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.SALE)
                .cashIn(soldPrice)
                .cashOut(0)
                .profit(soldPrice - sale.getPurchasePrice())
                .description(transactionDescription)
                .sale(sale)
                .staff(staff)
                .cashRegister(cashRegister)
                .build();

        sale.setSoldPrice(soldPrice);
        sale.setStatus(SaleStatus.SOLD);
        sale.setActive(false);
        sale.getTransactions().add(transaction);

        saleRepository.save(sale);
        cashRegisterService.save(cashRegister);

        return sale;
    }

    @Transactional
    public Sale create() {
        return null;
    }
}
