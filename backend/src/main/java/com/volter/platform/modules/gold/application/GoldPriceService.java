package com.volter.platform.modules.gold.application;

import com.volter.platform.modules.gold.application.dto.GoldPriceResponse;
import com.volter.shared.config.CacheConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

/**
 * Proxies the gold price from goldapi.io. The API key is kept server-side (never
 * shipped to the client).
 *
 * <p>{@link #current()} is {@code @Cacheable}, so the result is cached under the
 * {@code goldPrice} cache, which has a 1-hour TTL (see {@link CacheConfig}). The
 * rate-limited upstream is therefore hit at most once per hour and refreshes
 * automatically — no restart needed.
 */
@Service
public class GoldPriceService {

    private final RestClient restClient;
    private final String url;
    private final String apiKey;

    public GoldPriceService(@Value("${gold.api.url}") String url,
                            @Value("${gold.api.key:}") String apiKey) {
        this.restClient = RestClient.create();
        this.url = url;
        this.apiKey = apiKey;
    }

    @Cacheable(CacheConfig.GOLD_PRICE_CACHE)
    public GoldPriceResponse current() {
        try {
            return restClient.get()
                    .uri(url)
                    .header("x-access-token", apiKey)
                    .retrieve()
                    .body(GoldPriceResponse.class);
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Gold price is currently unavailable", ex);
        }
    }
}
