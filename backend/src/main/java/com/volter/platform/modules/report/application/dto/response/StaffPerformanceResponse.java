package com.volter.platform.modules.report.application.dto.response;

import java.time.LocalDate;

/**
 * A staff member's performance over a period, derived from the transactions they
 * performed. Profit is split into pawn (interest realized) and sale (margin); the
 * total is their sum (expenses are not deducted). Money figures are in denars.
 */
public record  StaffPerformanceResponse(
        Long staffId,
        LocalDate dateFrom,
        LocalDate dateTo,
        // money handled
        long revenue,            // money brought in (pawn redemptions/extensions + sales)
        long moneyGiven,         // money paid out to clients (pawn loans + item purchases)
        // profit (attributable)
        long pawnProfit,         // interest realized on extensions + redemptions
        long saleProfit,         // margin realized on sales
        long totalProfit,        // pawnProfit + saleProfit
        // activity counts
        long pawnsOpened,
        long pawnsExtended,
        long pawnsRedeemed,
        long salesCreated,
        long salesSold,
        long expensesRecorded,
        // extras
        long avgLoanSize,
        long discrepancyCount,
        long discrepancyTotal,   // signed (negative = net shortage)
        long underpricedSales,
        long underpaidRedemptions,
        long riskFlags           // underpricedSales + underpaidRedemptions
) {
}
