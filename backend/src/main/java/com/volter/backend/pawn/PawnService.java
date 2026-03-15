package com.volter.backend.pawn;

import com.volter.backend.cashRegister.CashRegister;
import com.volter.backend.cashRegister.CashRegisterService;
import com.volter.backend.customer.Customer;
import com.volter.backend.customer.CustomerService;
import com.volter.backend.exceptions.ResourceNotFoundException;
import com.volter.backend.item.Item;
import com.volter.backend.item.ItemService;
import com.volter.backend.item.enums.ItemType;
import com.volter.backend.pawn.dto.PawnCreationRequest;
import com.volter.backend.pawn.enums.PawnStatus;
import com.volter.backend.pawn.mapper.PawnMapper;
import com.volter.backend.pawnEvent.PawnEvent;
import com.volter.backend.pawnEvent.enums.PawnEventType;
import com.volter.backend.sale.Sale;
import com.volter.backend.sale.SaleService;
import com.volter.backend.staff.Staff;
import com.volter.backend.staff.StaffService;
import com.volter.backend.transaction.Transaction;
import com.volter.backend.transaction.TransactionService;
import com.volter.backend.transaction.enums.TransactionType;
import com.volter.backend.util.Validate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PawnService {

    private final Validate validate;
    private final PawnRepository pawnRepository;
    private final StaffService staffService;
    private final CashRegisterService cashRegisterService;
    private final SaleService saleService;
    private final CustomerService customerService;
    private final PawnMapper pawnMapper;
    private final ItemService itemService;
    private final TransactionService transactionService;

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
    public List<Pawn> getAll() {
        return pawnRepository.findAll();
    }

    /**
     * Redeems a pawn (client pays off the loan and retrieves their item)
     */
    @Transactional
    public Pawn redeem(Long id, Integer redeemAmount, String transactionDescription, Long cashRegisterId, Authentication authentication) {
        Staff staff = staffService.getById(validate.extractStaffId(authentication));
        Pawn pawn = getById(id);
        Customer customer = pawn.getCustomer();
        CashRegister cashRegister = cashRegisterService.getById(cashRegisterId);

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.PAWN_REDEMPTION)
                .cashIn(redeemAmount)
                .cashOut(0)
                .profit(redeemAmount - pawn.getAmount())
                .description(transactionDescription)
                .pawn(pawn)
                .staff(staff)
                .cashRegister(cashRegister)
                .build();

        PawnEvent pawnEvent = PawnEvent.builder()
                .type(PawnEventType.REDEEM)
                .note("Pawn redeemed with amount: " + redeemAmount)
                .pawn(pawn)
                .staff(staff)
                .build();

        pawn.setStatus(PawnStatus.REDEEMED);
        pawn.setActive(false);
        pawn.getPawnEvents().add(pawnEvent);

        customer.setRedeemCount(customer.getRedeemCount() + 1);

        cashRegister.setPawnCount(cashRegister.getPawnCount() - 1);
        cashRegister.setTotalPawnPayout(cashRegister.getTotalPawnPayout() - pawn.getAmount());
        cashRegister.setTotalInterestAmount(cashRegister.getTotalInterestAmount() - pawn.getInterest());
        cashRegister.setBalance(cashRegister.getBalance() - redeemAmount);
        if (pawn.getItem().getItemType() == ItemType.GOLD) {
            cashRegister.setTotalGoldWeightGrams(cashRegister.getTotalGoldWeightGrams() - pawn.getItem().getGoldItemDetails().getWeightGrams());
        }

        pawnRepository.save(pawn);                  // pawn event will be saved via cascade from pawn
        transactionService.save(transaction);
        customerService.save(customer);
        cashRegisterService.save(cashRegister);

        return pawn;
    }

    /**
     * Forfeits a pawn (client fails to redeem by maturity date, item is transferred to sale)
     */
    @Transactional
    public Pawn forfeit(Long id, String transactionDescription, Long cashRegisterId, Authentication authentication) {
        Staff staff = staffService.getById(validate.extractStaffId(authentication));
        Pawn pawn = getById(id);
        Customer customer = pawn.getCustomer();
        CashRegister cashRegister = cashRegisterService.getById(cashRegisterId);

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.PAWN_FORFEIT)
                .cashIn(0)
                .cashOut(0)
                .profit(0)
                .description(transactionDescription)
                .pawn(pawn)
                .staff(staff)
                .cashRegister(cashRegister)
                .build();

        PawnEvent pawnEvent = PawnEvent.builder()
                .type(PawnEventType.FORFEIT)
                .note("Pawn forfeited")
                .pawn(pawn)
                .staff(staff)
                .build();

        Sale sale = Sale.builder()
                .purchasePrice(pawn.getAmount())
                .customer(pawn.getCustomer())
                .item(pawn.getItem())
                .build();

        pawn.setStatus(PawnStatus.FORFEITED);
        pawn.setActive(false);
        pawn.getPawnEvents().add(pawnEvent);

        customer.setForfeitCount(customer.getForfeitCount() + 1);

        cashRegister.setPawnCount(cashRegister.getPawnCount() - 1);
        cashRegister.setTotalPawnPayout(cashRegister.getTotalPawnPayout() - pawn.getAmount());
        cashRegister.setTotalInterestAmount(cashRegister.getTotalInterestAmount() - pawn.getInterest());
        cashRegister.setSaleCount(cashRegister.getSaleCount() + 1);
        cashRegister.setTotalSalePayout(cashRegister.getTotalSalePayout() + pawn.getAmount());
        cashRegister.setBalance(cashRegister.getBalance());

        pawnRepository.save(pawn);                  // pawn event will be saved via cascade from pawn
        transactionService.save(transaction);
        saleService.save(sale);
        customerService.save(customer);
        cashRegisterService.save(cashRegister);

        return pawn;
    }

    /**
     * Renews a pawn (client pays interest to extend maturity date)
     */
    @Transactional
    public Pawn renew(Long id, Integer interest, String transactionDescription, Long cashRegisterId, Authentication authentication) {
        Staff staff = staffService.getById(validate.extractStaffId(authentication));
        Pawn pawn = getById(id);
        Customer customer = pawn.getCustomer();
        CashRegister cashRegister = cashRegisterService.getById(cashRegisterId);

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.PAWN_RENEWAL)
                .cashIn(interest)
                .cashOut(0)
                .profit(interest)
                .description(transactionDescription)
                .pawn(pawn)
                .staff(staff)
                .cashRegister(cashRegister)
                .build();

        PawnEvent pawnEvent = PawnEvent.builder()
                .type(PawnEventType.RENEW)
                .note("Pawn renewed with interest: " + interest)
                .pawn(pawn)
                .staff(staff)
                .build();

        float pawnDailyInterest = (float) pawn.getInterest() / pawn.getDurationDays();
        float carryOverDays = interest / pawnDailyInterest;

        pawn.setMaturityDate(pawn.getMaturityDate().plusDays(Math.round(carryOverDays)));
        pawn.getPawnEvents().add(pawnEvent);

        if (pawn.getMaturityDate().isBefore(LocalDate.now())) {
            customer.setAvgDaysLate((customer.getAvgDaysLate() * customer.getLateRenewalCount() + ChronoUnit.DAYS.between(pawn.getMaturityDate(), LocalDate.now())) / (customer.getLateRenewalCount() + 1));
            customer.setLateRenewalCount(customer.getLateRenewalCount() + 1);
        }
        else
            customer.setOnTimeRenewalCount(customer.getOnTimeRenewalCount() + 1);

        cashRegister.setBalance(cashRegister.getBalance() + interest);

        pawnRepository.save(pawn);                  // pawn event will be saved via cascade from pawn
        transactionService.save(transaction);
        customerService.save(customer);
        cashRegisterService.save(cashRegister);

        return pawn;
    }

    public Pawn save(Pawn pawn) {
        return pawnRepository.save(pawn);
    }

    public Pawn create(PawnCreationRequest request, String transactionDescription, Long cashRegisterId, Authentication authentication) {
        Staff staff = staffService.getById(validate.extractStaffId(authentication));
        Pawn pawn = pawnMapper.toEntity(request);
        Customer customer = pawn.getCustomer();
        Item item = pawn.getItem();
        CashRegister cashRegister = cashRegisterService.getById(cashRegisterId);


        PawnEvent pawnEvent = PawnEvent.builder()
                .type(PawnEventType.CREATE)
                .note("Pawn created with loan amount: " + pawn.getAmount())
                .pawn(pawn)
                .staff(staff)
                .build();

        pawn.getPawnEvents().add(pawnEvent);

        pawn = pawnRepository.save(pawn);      // pawn event will be saved via cascade from pawn
        // We need to save pawn before creating transaction because transaction has a reference to pawn and pawn needs to have an ID for the relationship to work.
        // For pawn event, we can rely on cascade from pawn, but for transaction we need to save it explicitly after pawn is saved.

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.PAWN_CREATION)
                .cashIn(0)
                .cashOut(pawn.getAmount())
                .profit(0)
                .description(transactionDescription)
                .pawn(pawn)
                .staff(staff)
                .cashRegister(cashRegister)
                .build();

        cashRegister.setPawnCount(cashRegister.getPawnCount() + 1);
        cashRegister.setTotalPawnPayout(cashRegister.getTotalPawnPayout() + pawn.getAmount());
        cashRegister.setTotalInterestAmount(cashRegister.getTotalInterestAmount() + pawn.getInterest());
        cashRegister.setBalance(cashRegister.getBalance() - pawn.getAmount());
        if (item.getItemType() == ItemType.GOLD) {
            cashRegister.setTotalGoldWeightGrams(cashRegister.getTotalGoldWeightGrams() + item.getGoldItemDetails().getWeightGrams());
        }

        transactionService.save(transaction);
        itemService.save(item);
        customerService.save(customer);
        cashRegisterService.save(cashRegister);

        return pawn;
    }

    public Pawn update() {
        return null;
    }


}
