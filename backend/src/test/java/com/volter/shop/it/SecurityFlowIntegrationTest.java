package com.volter.shop.it;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Drives the real Spring Security filter chain (JWT filter -> tenant filter -> authorization)
 * end to end via MockMvc against a real PostgreSQL, exercising the two-phase auth flow:
 * {@code login -> PRE_AUTH token -> select-shop -> ACCESS token} and the guards around it.
 *
 * Uses the V3 bootstrap seed: staff {@code user}/{@code password} assigned to shop {@code TEST}
 * with the {@code SUPER_ADMIN} role (all permissions).
 */
@AutoConfigureMockMvc
class SecurityFlowIntegrationTest extends AbstractIntegrationTest {

    private static final String USERNAME = "user";
    private static final String PASSWORD = "password";
    private static final String SHOP_CODE = "TEST";

    @Autowired
    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();

    // ----- login (phase 1) -----

    @Test
    void login_withValidCredentials_returnsPreAuthTokenAndAssignedShops() throws Exception {
        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preAuthToken").isNotEmpty())
                .andExpect(jsonPath("$.shops[?(@.code == '" + SHOP_CODE + "')]").exists());
    }

    @Test
    void login_withWrongPassword_isUnauthorized() throws Exception {
        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(USERNAME, "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    // ----- select-shop (phase 2) -----

    @Test
    void selectShop_withPreAuthToken_returnsAccessToken() throws Exception {
        String preAuth = preAuthToken();
        Long shopId = assignedShopId();

        mvc.perform(post("/auth/select-shop")
                        .header("Authorization", "Bearer " + preAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"shopId\":" + shopId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.shopId").value(shopId));
    }

    @Test
    void selectShop_toUnassignedShop_isUnauthorized() throws Exception {
        String preAuth = preAuthToken();

        mvc.perform(post("/auth/select-shop")
                        .header("Authorization", "Bearer " + preAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"shopId\":999999}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void selectShop_withAccessToken_isForbidden() throws Exception {
        // select-shop is restricted to ROLE_PRE_AUTH at the URL level; an ACCESS token must be rejected.
        String access = accessToken();

        mvc.perform(post("/auth/select-shop")
                        .header("Authorization", "Bearer " + access)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"shopId\":" + assignedShopId() + "}"))
                .andExpect(status().isForbidden());
    }

    // ----- business endpoints behind @PreAuthorize -----

    @Test
    void businessEndpoint_withoutToken_isUnauthorized() throws Exception {
        mvc.perform(get("/cash-registers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void businessEndpoint_withPreAuthToken_isForbidden() throws Exception {
        // Authenticated but only ROLE_PRE_AUTH: passes the URL filter, blocked by @PreAuthorize.
        mvc.perform(get("/cash-registers")
                        .header("Authorization", "Bearer " + preAuthToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void businessEndpoint_withAccessToken_isOk() throws Exception {
        // SUPER_ADMIN carries CASH_REGISTER_READ; tenant routes to shop_test.
        mvc.perform(get("/cash-registers")
                        .header("Authorization", "Bearer " + accessToken()))
                .andExpect(status().isOk());
    }

    // ----- helpers -----

    private String loginBody(String username, String password) throws Exception {
        return "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
    }

    private JsonNode login() throws Exception {
        MvcResult result = mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn();
        return mapper.readTree(result.getResponse().getContentAsString());
    }

    private String preAuthToken() throws Exception {
        return login().get("preAuthToken").asText();
    }

    private Long assignedShopId() throws Exception {
        for (JsonNode shop : login().get("shops")) {
            if (SHOP_CODE.equals(shop.get("code").asText())) {
                return shop.get("id").asLong();
            }
        }
        throw new IllegalStateException("Bootstrap shop " + SHOP_CODE + " not found in login response");
    }

    private String accessToken() throws Exception {
        MvcResult result = mvc.perform(post("/auth/select-shop")
                        .header("Authorization", "Bearer " + preAuthToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"shopId\":" + assignedShopId() + "}"))
                .andExpect(status().isOk())
                .andReturn();
        return mapper.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
    }
}
