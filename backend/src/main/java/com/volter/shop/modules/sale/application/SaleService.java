package com.volter.shop.modules.sale.application;

import com.volter.shop.modules.cashregister.application.CashRegisterService;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.customer.domain.factory.CustomerFactory;
import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.customer.application.CustomerService;
import com.volter.shop.modules.inventory.application.dtos.baseitem.response.ItemFactoryResult;
import com.volter.shop.modules.inventory.domain.factory.ItemFactory;
import com.volter.shop.modules.sale.application.dto.filter.SaleFilter;
import com.volter.shop.modules.sale.application.dto.request.SaleSellRequest;
import com.volter.shop.modules.sale.application.dto.result.SaleSellResult;
import com.volter.shop.modules.sale.domain.specification.SaleSpecification;
import com.volter.shop.shared.common.exceptions.ResourceNotFoundException;
import com.volter.shop.modules.inventory.application.ItemService;
import com.volter.shop.modules.sale.domain.model.Sale;
import com.volter.shop.modules.sale.domain.repository.SaleRepository;
import com.volter.shop.modules.sale.application.dto.request.SaleCreationRequest;
import com.volter.shop.modules.sale.infrastructure.SaleMapper;
import com.volter.shop.modules.staff.domain.model.Staff;
import com.volter.shop.modules.staff.application.StaffService;
import com.volter.shop.modules.transaction.application.TransactionService;
import com.volter.shop.shared.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final StaffService staffService;
    private final SaleRepository saleRepository;
    private final CashRegisterService cashRegisterService;
    private final SaleMapper saleMapper;
    private final TransactionService transactionService;
    private final ItemService itemService;
    private final CustomerService customerService;
    private final CustomerFactory customerFactory;
    private final ItemFactory itemFactory;

    @Transactional
    public Sale save(Sale sale) {
        return saleRepository.save(sale);
    }

    /**
     * Retrieves all sales
     */
    @Transactional
    public Page<Sale> getAll(SaleFilter filter, Pageable pageable) {
        Specification<Sale> spec = SaleSpecification.withFilters(filter);
        return saleRepository.findAll(spec, pageable);
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
    public Sale sell(Long id, SaleSellRequest request) {
        Staff staff = staffService.getCurrentStaff();
        Sale sale = getById(id);
        CashRegisterSession cashRegisterSession = cashRegisterService.getOpenSessionByStaff(staff.getId());

        SaleSellResult result = sale.sell(new Money(request.soldPrice()), request.transactionDescription(), cashRegisterSession, staff);
        saleRepository.save(sale);

        cashRegisterSession.recordTransaction(result.transaction());
        cashRegisterService.saveSession(cashRegisterSession);

        return sale;
    }

    @Transactional
    public Sale create(SaleCreationRequest request) {
        Staff staff = staffService.getCurrentStaff();
        CashRegisterSession cashRegisterSession = cashRegisterService.getOpenSessionByStaff(staff.getId());
        Customer customer = customerFactory.createOrGetCustomer(request.getCustomer());
        ItemFactoryResult itemFactoryResult = itemFactory.createOrGetItem(request.getItem());

        Sale sale = Sale.create(request, cashRegisterSession, staff, itemFactoryResult.item(), customer);
        saleRepository.save(sale);

        customer.saleAction();
        customerService.save(customer);

        if (itemFactoryResult.isNewItem())
            itemService.save(itemFactoryResult.item());

        cashRegisterSession.recordTransaction(sale.getInitialTransaction());
        cashRegisterService.saveSession(cashRegisterSession);

        return sale;
    }
}
