package com.volter.shop.modules.pawn.application;

import com.volter.identity.application.IdentityUserService;
import com.volter.identity.domain.model.IdentityUser;
import com.volter.shop.modules.alert.application.dto.RiskAlertCreationRequest;
import com.volter.shop.modules.cashregister.application.CashRegisterService;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.customer.domain.factory.CustomerFactory;
import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.customer.application.CustomerService;
import com.volter.shop.modules.inventory.application.dtos.baseitem.response.ItemFactoryResult;
import com.volter.shop.modules.inventory.domain.factory.ItemFactory;
import com.volter.shop.modules.pawn.application.dto.filter.PawnFilter;
import com.volter.shop.modules.pawn.application.dto.request.*;
import com.volter.shop.modules.pawn.application.dto.result.PawnModificationResult;
import com.volter.shop.modules.pawn.application.dto.result.PawnRedemptionResult;
import com.volter.shop.modules.pawn.application.dto.result.PawnRenewalResult;
import com.volter.shop.modules.pawn.domain.model.enums.PawnTransactionAction;
import com.volter.shop.modules.pawn.domain.specification.PawnSpecification;
import com.volter.shop.modules.sale.application.dto.dto.SaleFromForfeitedPawnDTO;
import com.volter.shop.modules.sale.domain.model.enums.SaleTransactionAction;
import com.volter.shop.shared.common.exceptions.ResourceNotFoundException;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.inventory.application.ItemService;
import com.volter.shop.modules.alert.domain.model.RiskAlert;
import com.volter.shop.modules.alert.application.RiskAlertService;
import com.volter.shop.modules.alert.domain.model.enums.RiskAlertSeverity;
import com.volter.shop.modules.alert.domain.model.enums.RiskAlertType;
import com.volter.shop.modules.pawn.domain.model.Pawn;
import com.volter.shop.modules.pawn.domain.repository.PawnRepository;
import com.volter.shop.modules.sale.domain.model.Sale;
import com.volter.shop.modules.sale.application.SaleService;
import com.volter.shop.modules.staff.domain.model.Staff;
import com.volter.shop.modules.staff.application.StaffService;
import com.volter.identity.domain.model.enums.RoleEnum;
import com.volter.shop.shared.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PawnService {

    private final PawnRepository pawnRepository;
    private final StaffService staffService;
    private final IdentityUserService identityUserService;
    private final CashRegisterService cashRegisterService;
    private final SaleService saleService;
    private final CustomerService customerService;
    private final ItemService itemService;
    private final RiskAlertService riskAlertService;
    private final ItemFactory itemFactory;
    private final CustomerFactory customerFactory;

    /**
     * Retrieves a pawn by its ID
     */
    @Transactional
    public Pawn getById(Long id) {
        return pawnRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Pawn with ID " + id + " not found")
        );
    }

    /**
     * Retrieves all pawns
     */
    @Transactional
    public Page<Pawn> getAll(PawnFilter filter, Pageable pageable) {
        Specification<Pawn> spec = PawnSpecification.withFilters(filter);
        return pawnRepository.findAll(spec, pageable);
    }

    @Transactional
    public List<Pawn> getAllMaturingWithinDays(int days) {
        LocalDate date = LocalDate.now().plusDays(days);
        return pawnRepository.findByPeriod_MaturityDateBefore(date);
    }

    /**
     * Retrieves all pawns for a specific customer
     */
    @Transactional
    public List<Pawn> getByCustomerId(Long customerId) {
        return pawnRepository.findByCustomer_Id(customerId);
    }

    /**
     * Redeems a pawn (client pays off the loan and retrieves their item)
     */
    @Transactional
    public Pawn redeem(Long id, PawnRedemptionRequest request) {
        Staff staff = staffService.getCurrentStaff();
        IdentityUser identityUser = identityUserService.getCurrentIdentityUser();
        RoleEnum role = identityUser.getRole().getName();
        Pawn pawn = getById(id);
        Customer customer = pawn.getCustomer();
        CashRegisterSession cashRegisterSession = cashRegisterService.getOpenSessionByStaff(staff.getId());


        PawnRedemptionResult pawnRedemptionResult = pawn.redeem(new Money(request.paidAmount()), request.transactionDescription(), cashRegisterSession, staff);
        pawnRepository.save(pawn);

        customer.pawnAction(PawnTransactionAction.REDEMPTION);
        customerService.save(customer);

        cashRegisterSession.recordTransaction(pawnRedemptionResult.transaction());
        cashRegisterService.saveSession(cashRegisterSession);


        if (!List.of(RoleEnum.MANAGER, RoleEnum.ADMIN).contains(role) && pawnRedemptionResult.underpaid()) {
            RiskAlert riskAlert = RiskAlert.create(
                    new RiskAlertCreationRequest(
                        RiskAlertType.TRANSACTION_ANOMALY,
                        RiskAlertSeverity.HIGH,
                        null,
                        "Pawn redeemed for less than total owed amount (principal + interest)"
                    ),
                    pawnRedemptionResult.transaction(),
                    staff.getManager(),
                    pawnRedemptionResult.event()
            );

            riskAlertService.save(riskAlert);
        }

        return pawn;
    }

    /**
     * Forfeits a pawn (client fails to redeem by maturity date, item is transferred to sale)
     */
    @Transactional
    public Pawn forfeit(Long id, PawnForfeitureRequest request) {
        Staff staff = staffService.getCurrentStaff();
        Pawn pawn = getById(id);
        Customer customer = pawn.getCustomer();
        CashRegisterSession cashRegisterSession = cashRegisterService.getOpenSessionByStaff(staff.getId());

        pawn.forfeit(request.transactionDescription(), cashRegisterSession, staff);
        pawnRepository.save(pawn);

        Sale sale = Sale.moveFromPawn(
                new SaleFromForfeitedPawnDTO(
                    SaleTransactionAction.CREATION,
                    pawn.getAmount().amount()
                ),
                cashRegisterSession,
                staff,
                pawn.getItem(),
                customer
        );
        saleService.save(sale);

        customer.pawnAction(PawnTransactionAction.FORFEITURE);
        customerService.save(customer);

        return pawn;
    }

    /**
     * Renews a pawn (client pays interest to extend maturity date)
     */
    @Transactional
    public Pawn renew(Long id, PawnRenewalRequest request) {
        Staff staff = staffService.getCurrentStaff();
        Pawn pawn = getById(id);
        Customer customer = pawn.getCustomer();
        CashRegisterSession cashRegisterSession = cashRegisterService.getOpenSessionByStaff(staff.getId());


        PawnRenewalResult pawnRenewalResult = pawn.renew(new Money(request.interest()), request.transactionDescription(), cashRegisterSession, staff);
        pawnRepository.save(pawn);

        customer.pawnAction(PawnTransactionAction.RENEWAL, ChronoUnit.DAYS.between(pawn.getPeriod().maturityDate(), LocalDate.now()));
        customerService.save(customer);

        cashRegisterSession.recordTransaction(pawnRenewalResult.transaction());
        cashRegisterService.saveSession(cashRegisterSession);

        return pawn;
    }

    @Transactional
    public Pawn save(Pawn pawn) {
        return pawnRepository.save(pawn);
    }

    @Transactional
    public Pawn create(PawnCreationRequest request) {
        Staff staff = staffService.getCurrentStaff();
        CashRegisterSession cashRegisterSession = cashRegisterService.getOpenSessionByStaff(staff.getId());
        Customer customer = customerFactory.createOrGetCustomer(request.getCustomer());
        ItemFactoryResult itemFactoryResult = itemFactory.createOrGetItem(request.getItem());

        Pawn pawn = Pawn.create(
                request,
                cashRegisterSession,
                staff,
                itemFactoryResult.item(),
                customer
        );
        pawn = pawnRepository.save(pawn);

        customer.pawnAction(PawnTransactionAction.CREATION);
        customerService.save(customer);

        if (itemFactoryResult.isNewItem())
            itemService.save(itemFactoryResult.item());

        cashRegisterSession.recordTransaction(pawn.getInitialTransaction());
        cashRegisterService.saveSession(cashRegisterSession);

        return pawn;
    }

    @Transactional
    public Pawn modify(Long pawnId, PawnModificationRequest request) {
        Staff staff = staffService.getCurrentStaff();
        Pawn pawn = pawnRepository.findById(pawnId).orElseThrow(
                () -> new ResourceNotFoundException("Pawn with ID " + pawnId + " not found")
        );
        Item item = pawn.getItem();
        CashRegisterSession cashRegisterSession = cashRegisterService.getOpenSessionByStaff(staff.getId());

        PawnModificationResult pawnModificationResult = pawn.modify(request, cashRegisterSession, staff);
        pawnRepository.save(pawn);

        pawn.getItem().modify(request.itemModificationRequest());
        itemService.save(item);

        cashRegisterSession.recordTransaction(pawnModificationResult.transaction());
        cashRegisterService.saveSession(cashRegisterSession);

        RiskAlert riskAlert = RiskAlert.builder()
                .type(RiskAlertType.PAWN_MODIFICATION)
                .severity(RiskAlertSeverity.MEDIUM)
                .summary("Pawn with ID " + pawnId + " was modified")
                .transaction(pawnModificationResult.transaction())
                .pawnEvent(pawnModificationResult.event())
                .manager(staff.getManager())
                .build();
        riskAlertService.save(riskAlert);

        return pawn;
    }


}
