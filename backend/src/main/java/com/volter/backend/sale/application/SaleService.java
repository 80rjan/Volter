package com.volter.backend.sale.application;

import com.volter.backend.cashRegister.domain.model.CashRegister;
import com.volter.backend.cashRegister.application.CashRegisterService;
import com.volter.backend.customer.domain.model.Customer;
import com.volter.backend.customer.application.CustomerService;
import com.volter.backend.common.exceptions.ResourceNotFoundException;
import com.volter.backend.item.domain.model.Item;
import com.volter.backend.item.application.ItemService;
import com.volter.backend.sale.domain.model.Sale;
import com.volter.backend.sale.domain.repository.SaleRepository;
import com.volter.backend.sale.application.dto.SaleCreationRequest;
import com.volter.backend.sale.domain.model.enums.SaleStatus;
import com.volter.backend.sale.infrastructure.SaleMapper;
import com.volter.backend.staff.Staff;
import com.volter.backend.staff.StaffService;
import com.volter.backend.transaction.SaleTransaction;
import com.volter.backend.transaction.application.TransactionService;
import com.volter.backend.transaction.domain.model.enums.TransactionAction;
import com.volter.backend.common.utils.Validate;
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
    private final SaleMapper saleMapper;
    private final TransactionService transactionService;
    private final ItemService itemService;
    private final CustomerService customerService;

    @Transactional
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
     * Retrieves all sales by Customer ID
     */
    @Transactional
    public List<Sale> getByCustomerId(Long customerId) {
        return saleRepository.findByCustomer_Id(customerId);
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

        SaleTransaction transaction = SaleTransaction.builder()
                .action(TransactionAction.SALE)
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

        saleRepository.save(sale);
        transactionService.save(transaction);
        cashRegisterService.save(cashRegister);

        return sale;
    }

    @Transactional
    public Sale create(SaleCreationRequest request, String transactionDescription, Long cashRegisterId, Authentication authentication) {
        Staff staff = staffService.getById(validate.extractStaffId(authentication));
        Sale sale = saleMapper.toEntity(request);
        Customer customer = sale.getCustomer();
        Item item = sale.getItem();
        CashRegister cashRegister = cashRegisterService.getById(cashRegisterId);

        SaleTransaction transaction = SaleTransaction.builder()
                .action(TransactionAction.PURCHASE)
                .cashIn(0)
                .cashOut(sale.getPurchasePrice())
                .profit(0)
                .description(transactionDescription)
                .sale(sale)
                .staff(staff)
                .cashRegister(cashRegister)
                .build();

        cashRegister.setSaleCount(cashRegister.getSaleCount() + 1);
        cashRegister.setTotalSalePayout(cashRegister.getTotalSalePayout() + sale.getPurchasePrice());
        cashRegister.setBalance(cashRegister.getBalance() - sale.getPurchasePrice());

        saleRepository.save(sale);
        customerService.save(customer);
        itemService.save(item);
        transactionService.save(transaction);
        cashRegisterService.save(cashRegister);

        return sale;
    }
}
