package com.volter.shop.modules.gold.web.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoldPriceResponse(
        String currency,
        @JsonProperty("price_gram_24k") double priceGram24k,
        @JsonProperty("price_gram_22k") double priceGram22k,
        @JsonProperty("price_gram_21k") double priceGram21k,
        @JsonProperty("price_gram_20k") double priceGram20k,
        @JsonProperty("price_gram_18k") double priceGram18k,
        @JsonProperty("price_gram_16k") double priceGram16k,
        @JsonProperty("price_gram_14k") double priceGram14k,
        @JsonProperty("price_gram_10k") double priceGram10k
) {
}
