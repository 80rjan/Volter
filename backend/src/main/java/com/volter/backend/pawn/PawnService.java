package com.volter.backend.pawn;

import com.volter.backend.cashRegister.CashRegister;
import com.volter.backend.cashRegister.CashRegisterService;
import com.volter.backend.exceptions.ResourceNotFoundException;
import com.volter.backend.item.enums.ItemType;
import com.volter.backend.pawn.enums.PawnStatus;
import com.volter.backend.pawnEvent.PawnEvent;
import com.volter.backend.pawnEvent.enums.PawnEventType;
import com.volter.backend.sale.Sale;
import com.volter.backend.sale.SaleService;
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
public class PawnService {

    private final Validate validate;
    private final PawnRepository pawnRepository;
    private final StaffService staffService;
    private final CashRegisterService cashRegisterService;
    private final SaleService saleService;

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
        pawn.getTransactions().add(transaction);
        pawn.getPawnEvents().add(pawnEvent);

        cashRegister.setPawnCount(cashRegister.getPawnCount() - 1);
        cashRegister.setTotalPawnPayout(cashRegister.getTotalPawnPayout() - pawn.getAmount());
        cashRegister.setTotalInterestAmount(cashRegister.getTotalInterestAmount() - pawn.getInterest());
        cashRegister.setBalance(cashRegister.getBalance() - redeemAmount);
        if (pawn.getItem().getItemType() == ItemType.GOLD) {
            cashRegister.setTotalGoldWeightGrams(cashRegister.getTotalGoldWeightGrams() - pawn.getItem().getGoldItemDetails().getWeightGrams());
        }

        pawnRepository.save(pawn);                  // transaction and pawn event will be saved via cascade from pawn
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
                .item(pawn.getItem())
                .customer(pawn.getCustomer())
                .item(pawn.getItem())
                .build();

        pawn.setStatus(PawnStatus.FORFEITED);
        pawn.setActive(false);
        pawn.getTransactions().add(transaction);
        pawn.getPawnEvents().add(pawnEvent);

        cashRegister.setPawnCount(cashRegister.getPawnCount() - 1);
        cashRegister.setTotalPawnPayout(cashRegister.getTotalPawnPayout() - pawn.getAmount());
        cashRegister.setTotalInterestAmount(cashRegister.getTotalInterestAmount() - pawn.getInterest());
        cashRegister.setSaleCount(cashRegister.getSaleCount() + 1);
        cashRegister.setTotalSalePayout(cashRegister.getTotalSalePayout() + pawn.getAmount());
        cashRegister.setBalance(cashRegister.getBalance());

        saleService.save(sale);
        pawnRepository.save(pawn);                  // transaction and pawn event will be saved via cascade from pawn
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
                .type(PawnEventType.RENEWAL)
                .note("Pawn renewed with interest: " + interest)
                .pawn(pawn)
                .staff(staff)
                .build();

        float pawnDailyInterest = (float) pawn.getInterest() / pawn.getDurationDays();
        float carryOverDays = interest / pawnDailyInterest;

        pawn.setMaturityDate(pawn.getMaturityDate().plusDays(Math.round(carryOverDays)));
        pawn.getTransactions().add(transaction);
        pawn.getPawnEvents().add(pawnEvent);

        cashRegister.setBalance(cashRegister.getBalance() + interest);

        pawnRepository.save(pawn);                  // transaction and pawn event will be saved via cascade from pawn
        cashRegisterService.save(cashRegister);

        return pawn;
    }

    public Pawn save(Pawn pawn) {
        return pawnRepository.save(pawn);
    }

    public Pawn create() {
        return null;
    }

    public Pawn update() {
        return null;
    }


}
