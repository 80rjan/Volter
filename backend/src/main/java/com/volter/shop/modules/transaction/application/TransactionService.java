package com.volter.shop.modules.transaction.application;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.transaction.application.dto.TransactionFilterRequest;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionType;
import com.volter.shop.modules.transaction.domain.repository.TransactionRepository;
import com.volter.shop.modules.transaction.domain.specification.TransactionSpecification;
import com.volter.shop.shared.valueobject.Money;
import com.volter.shared.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final StaffService staffService;

    /**
     * Transactions the caller may see: their own plus those of every staff member
     * below them in the management tree (recursive), narrowed by the filter.
     */
    public Page<Transaction> list(TransactionFilterRequest filter, Pageable pageable, Long staffId) {
        List<Long> visibleStaffIds = new ArrayList<>(staffService.findSubordinateStaffIds(staffId));
        visibleStaffIds.add(staffId);
        return transactionRepository.findAll(
                TransactionSpecification.matches(filter).and(TransactionSpecification.staffIdIn(visibleStaffIds)),
                pageable);
    }

    /**
     * A single transaction, only if it is the caller's own or was made by someone they manage.
     */
    public Transaction get(Long id, Long staffId) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + id));
        if (!Objects.equals(transaction.getStaffId(), staffId) && !staffService.isManagerOf(staffId, transaction.getStaffId())) {
            throw new AccessDeniedException("Cannot access a transaction made by another staff member");
        }
        return transaction;
    }

    // CROSS MODULE OPERATIONS
    /**
     * The transactions module write entry point.
     * This is for recording transactions from other modules, instead of letting them touch the repository directly.
     */
    @Transactional
    public Transaction record(Long staffId, CashRegisterSession session, TransactionType type,
                              Money amount, TransactionDirection direction, String description) {
        return transactionRepository.save(
                Transaction.create(staffId, session, type, amount, direction, description));
    }
}
