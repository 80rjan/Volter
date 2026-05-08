package com.volter.identity.application;

import com.volter.identity.domain.model.Shop;
import com.volter.identity.domain.repository.ShopRepository;
import com.volter.shared.config.SchemaInitializer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final SchemaInitializer schemaInitializer;

    public Shop createShop(String name, String schemaName) {
        Shop shop = Shop.builder()
                .name(name)
                .schemaName(schemaName)
                .build();
        shopRepository.save(shop);                              // inserts row in public.shop
        schemaInitializer.initializeTenantSchema(schemaName);   // creates the PG schema + tables
        return shop;
    }
}

