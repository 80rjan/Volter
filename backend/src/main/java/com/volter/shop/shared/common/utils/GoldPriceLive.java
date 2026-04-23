package com.volter.shop.shared.common.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

import org.json.JSONObject;

public class GoldPriceLive {

    private static String cachedPrice = null;
    private static long cachedTimestamp = 0;
    private static final long CACHE_DURATION_MS = 60 * 60 * 1000; // 1 hour

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static String fetchGoldPriceLive() {
        long now = Instant.now().toEpochMilli();

        // Return cached price if within cache duration
        if (cachedPrice != null && (now - cachedTimestamp < CACHE_DURATION_MS)) {
            return cachedPrice;
        }

        try {
            String apiKey = System.getenv("GOLD_API_KEY");
            if (apiKey == null || apiKey.isEmpty()) {
                throw new RuntimeException("GOLD_API_KEY environment variable not set");
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://goldpricez.com/api/rates/currency/eur/measure/gram"))
                    .header("X-API-KEY", apiKey)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            JSONObject data = new JSONObject(body);

            // Handle double-encoded JSON
            if (data.has("gram_in_eur") && data.get("gram_in_eur") instanceof String) {
                data = new JSONObject(data.getString("gram_in_eur"));
            }

            if (!data.has("gram_in_eur")) {
                return cachedPrice; // fallback to cached
            }

            String pricePerGram = String.format("%.2f", data.getDouble("gram_in_eur"));

            // Update cache
            cachedPrice = pricePerGram;
            cachedTimestamp = now;

            return pricePerGram;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return cachedPrice; // fallback to cached
        }
    }
}