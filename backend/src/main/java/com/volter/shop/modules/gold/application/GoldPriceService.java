package com.volter.shop.modules.gold.application;

import com.volter.shop.modules.gold.web.response.GoldPriceResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GoldPriceService {

    private static final String BASE_URL = "https://www.goldapi.io/api";

    private final RestClient restClient;
    private final String currency;

    public GoldPriceService(@Value("${gold.api.key}") String apiKey,
                            @Value("${gold.api.currency:USD}") String currency) {
        this.currency = currency;
        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader("x-access-token", apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Cacheable("goldPrice")
    public GoldPriceResponse getLivePrice() {
        GoldApiResponse raw = restClient.get()
                .uri("/XAU/{currency}", currency)
                .retrieve()
                .body(GoldApiResponse.class);

        return new GoldPriceResponse(
                currency,
                raw.price_gram_24k(),
                raw.price_gram_22k(),
                raw.price_gram_21k(),
                raw.price_gram_20k(),
                raw.price_gram_18k(),
                raw.price_gram_16k(),
                raw.price_gram_14k(),
                raw.price_gram_10k()
        );
    }

    // Internal record matching the GoldAPI.io JSON response fields
    private record GoldApiResponse(
            double price_gram_24k,
            double price_gram_22k,
            double price_gram_21k,
            double price_gram_20k,
            double price_gram_18k,
            double price_gram_16k,
            double price_gram_14k,
            double price_gram_10k
    ) {}
}
