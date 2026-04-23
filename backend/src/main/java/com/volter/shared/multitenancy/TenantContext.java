package com.volter.shared.multitenancy;

public class TenantContext {
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    public static final String GLOBAL_SCHEMA = "public";

    public static void setCurrentTenant(String schema) {
        CURRENT_TENANT.set(schema);
    }

    public static String getCurrentTenant() {
        return CURRENT_TENANT.get() != null
                ? CURRENT_TENANT.get()
                : GLOBAL_SCHEMA;
    }

    public static void clear() {
        CURRENT_TENANT.remove(); // prevents memory leaks in thread pools
    }
}
