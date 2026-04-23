package com.volter.shared.multitenancy;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolver
        implements CurrentTenantIdentifierResolver<String> {

    @Override
    public String resolveCurrentTenantIdentifier() {
        return TenantContext.getCurrentTenant(); // returns "shop_skopje_centar", "shop_skopje_vlae", "public", etc.
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
