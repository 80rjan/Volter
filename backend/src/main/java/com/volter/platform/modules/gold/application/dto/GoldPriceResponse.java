package com.volter.platform.modules.gold.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Subset of the goldapi.io response that the frontend consumes. Field names are
 * snake_case to match both the upstream payload (for deserialization) and what
 * the client expects (for serialization), so no field mapping is needed.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GoldPriceResponse(
        String currency,
        double price_gram_24k,
        double price_gram_22k,
        double price_gram_21k,
        double price_gram_20k,
        double price_gram_18k,
        double price_gram_16k,
        double price_gram_14k,
        double price_gram_10k
) {
}
