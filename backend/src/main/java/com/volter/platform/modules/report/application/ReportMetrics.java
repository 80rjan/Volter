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

    public static long netProfit(Map<String, Object> payload) {
        return sumSection(payload, "pawns", "net") + sumSection(payload, "sales", "net") - totalExpenses(payload);
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
