package com.volter.platform.modules.report.application;

import java.util.Map;

/**
 * Derives the headline figures (revenue, money given to clients, expenses, net
 * profit) from a monthly/period summary payload. Shared by the report list
 * mapper and the on-the-fly period report.
 */
public final class ReportMetrics {

    private ReportMetrics() {}

    public static long totalRevenue(Map<String, Object> payload) {
        return sumSection(payload, "pawns", "inflow") + sumSection(payload, "sales", "inflow");
    }

    public static long moneyGivenToClients(Map<String, Object> payload) {
        return sumSection(payload, "pawns", "outflow") + sumSection(payload, "sales", "outflow");
    }

    public static long totalExpenses(Map<String, Object> payload) {
        return sumSection(payload, "expenses", "amount");
    }

    /**
     * Realized profit minus expenses: pawn interest (provision) + sale margin − expenses.
     * The provision and margin are stored as scalars in the payload; this deliberately does
     * NOT use the per-item cash-flow net (inflow − outflow), which would wrongly subtract the
     * loan principal handed to clients and ignore the cost of goods sold.
     */
    public static long netProfit(Map<String, Object> payload) {
        return scalar(payload, "pawnProvision") + scalar(payload, "saleMargin") - totalExpenses(payload);
    }

    /** Reads a top-level scalar long from the payload (0 if absent or not a number). */
    public static long scalar(Map<String, Object> payload, String key) {
        if (payload != null && payload.get(key) instanceof Number n) return n.longValue();
        return 0L;
    }

    /** Sums {@code field} across every entry of {@code section} in the payload (0 if absent). */
    public static long sumSection(Map<String, Object> payload, String section, String field) {
        if (payload == null || !(payload.get(section) instanceof Map<?, ?> entries)) return 0L;
        long sum = 0L;
        for (Object value : entries.values()) {
            if (value instanceof Map<?, ?> entry && entry.get(field) instanceof Number n) {
                sum += n.longValue();
            }
        }
        return sum;
    }
}
