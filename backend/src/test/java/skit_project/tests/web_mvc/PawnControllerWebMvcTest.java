package skit_project.tests.web_mvc;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.shared.security.StaffPrincipal;
import com.volter.shop.modules.pawn.application.PawnService;
import com.volter.shop.modules.pawn.application.dto.PawnContractDetailedResponse;
import com.volter.shop.modules.pawn.application.dto.PawnContractExtensionResponse;
import com.volter.shop.modules.pawn.application.dto.PawnContractResponse;
import com.volter.shop.modules.pawn.application.dto.PawnSummaryResponse;
import com.volter.shop.modules.pawn.domain.model.PawnContract;
import com.volter.shop.modules.pawn.domain.model.PawnContractExtension;
import com.volter.shop.modules.pawn.domain.model.enums.PawnContractStatus;
import com.volter.shop.modules.pawn.infrastructure.mapper.PawnContractMapper;
import com.volter.shop.modules.pawn.web.PawnController;
import com.volter.shared.web.exception.GlobalExceptionHandler;
import com.volter.shared.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PawnController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = PawnControllerWebMvcTest.TestApp.class)
@Import({PawnController.class, GlobalExceptionHandler.class})
@DisplayName("Spring Boot @WebMvcTest — PawnController (all endpoints)")
class PawnControllerWebMvcTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EnableWebSecurity
    static class TestApp {
    }

    @Autowired private MockMvc mockMvc;

    @MockitoBean private PawnService pawnService;
    @MockitoBean private PawnContractMapper pawnContractMapper;
    @MockitoBean private StaffService staffService;

    private static final long STAFF_ID = 9L;
    private static final String STAFF_NAME = "Марко Марков";

    @BeforeEach
    void putStaffPrincipalInContext() {
        StaffPrincipal principal = StaffPrincipal.access(
                STAFF_ID, "marko", 1L, "shop_1", Set.of("ADMIN"),
                Set.of("PAWN_READ", "PAWN_WRITE", "PAWN_UPDATE", "PAWN_FORFEIT"));
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(principal, null));
        when(staffService.findStaffNames(anySet())).thenReturn(Map.of(STAFF_ID, STAFF_NAME));
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    /** A PawnContract mock created by STAFF_ID (so the created-by name lookup resolves). */
    private PawnContract mockContract() {
        PawnContract contract = Mockito.mock(PawnContract.class);
        when(contract.getCreatedByStaffId()).thenReturn(STAFF_ID);
        return contract;
    }

    private PawnContractResponse sampleResponse() {
        return new PawnContractResponse(
                5L, 1L, "Ана Ангелова", null, STAFF_ID, STAFF_NAME,
                1000, 100, 30,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31),
                PawnContractStatus.ACTIVE, 0L, null, null, null);
    }

    // ---------- GET /pawns ----------------------------------------------------------------

    @Test
    @DisplayName("GET /pawns -> 200 with a paged list of contracts")
    void list_returnsPagedContracts() throws Exception {
        PawnContract contract = mockContract();
        Page<PawnContract> page = new PageImpl<>(List.of(contract));
        when(pawnService.list(any(), any(), eq(STAFF_ID))).thenReturn(page);
        when(pawnContractMapper.toResponse(any(), any())).thenReturn(sampleResponse());

        mockMvc.perform(get("/pawns"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(5))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    // ---------- GET /pawns/summary --------------------------------------------------------

    @Test
    @DisplayName("GET /pawns/summary -> 200 with the aggregate totals")
    void summary_returnsTotals() throws Exception {
        when(pawnService.summarize(any(), eq(STAFF_ID)))
                .thenReturn(new PawnSummaryResponse(3, 3000, 300, 5.0, 120));

        mockMvc.perform(get("/pawns/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3))
                .andExpect(jsonPath("$.totalPrincipal").value(3000))
                .andExpect(jsonPath("$.totalGoldGrams").value(5.0));
    }

    // ---------- GET /pawns/{id} -----------------------------------------------------------

    @Test
    @DisplayName("GET /pawns/{id} -> 200 with the contract JSON")
    void get_returnsContractJson() throws Exception {
        PawnContract contract = mockContract();
        when(pawnService.get(5L)).thenReturn(contract);
        PawnContractDetailedResponse dto = new PawnContractDetailedResponse(
                5L, null, null, STAFF_ID, STAFF_NAME,
                1000, 100, 30,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31), LocalDate.of(2025, 1, 31),
                PawnContractStatus.ACTIVE, 0L, null, null, List.of(), null, null);
        when(pawnContractMapper.toDetailedResponse(any(), any())).thenReturn(dto);

        mockMvc.perform(get("/pawns/5"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.principalAmount").value(1000))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.createdByStaffName").value(STAFF_NAME));
    }

    @Test
    @DisplayName("GET /pawns/{id} for a missing contract -> 404 (exception handler maps it)")
    void get_missing_returns404() throws Exception {
        when(pawnService.get(5L)).thenThrow(new ResourceNotFoundException("Pawn contract not found: 5"));

        mockMvc.perform(get("/pawns/5"))
                .andExpect(status().isNotFound());
    }

    // ---------- POST /pawns ---------------------------------------------------------------

    @Test
    @DisplayName("POST /pawns with a valid body -> 201 Created with the contract JSON")
    void create_validBody_returns201() throws Exception {
        PawnContract contract = mockContract();
        when(pawnService.create(any(), eq(STAFF_ID))).thenReturn(contract);
        when(pawnContractMapper.toResponse(any(), any())).thenReturn(sampleResponse());

        String body = """
                {
                  "customerId": 1,
                  "item": {"type":"GOLD","origin":"PAWN","initialStatus":"IN_PAWN"},
                  "principalAmount": 1000,
                  "interestAmount": 100,
                  "termDays": 30,
                  "issueDate": "2025-01-01",
                  "cashRegisterSessionId": 77
                }
                """;

        mockMvc.perform(post("/pawns").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("POST /pawns with an invalid body -> 400 (bean validation at the web boundary)")
    void create_invalidBody_returns400() throws Exception {
        // Empty JSON: all @NotNull fields of PawnCreateRequest are missing -> 400 before the
        // controller body runs (so no service interaction is needed).
        mockMvc.perform(post("/pawns").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ---------- PATCH /pawns/{id}/contract ------------------------------------------------

    @Test
    @DisplayName("PATCH /pawns/{id}/contract with a valid body -> 200 with the updated contract")
    void updateContract_validBody_returns200() throws Exception {
        PawnContract contract = mockContract();
        when(pawnService.updateContract(eq(5L), any(), eq(STAFF_ID))).thenReturn(contract);
        when(pawnContractMapper.toResponse(any(), any())).thenReturn(sampleResponse());

        String body = """
                {"principalAmount":1200,"interestAmount":120,"termDays":30,
                 "issueDate":"2025-01-01","cashRegisterSessionId":77}
                """;

        mockMvc.perform(patch("/pawns/5/contract").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    // ---------- POST /pawns/{id}/extend ---------------------------------------------------

    @Test
    @DisplayName("POST /pawns/{id}/extend with a valid body -> 200 with the extension JSON")
    void extend_validBody_returns200() throws Exception {
        when(pawnService.extend(eq(5L), any(), eq(STAFF_ID)))
                .thenReturn(Mockito.mock(PawnContractExtension.class));
        when(pawnContractMapper.toResponse(any(PawnContractExtension.class)))
                .thenReturn(new PawnContractExtensionResponse(
                        1L, LocalDate.of(2025, 1, 31), LocalDate.of(2025, 2, 20), 50, 0, null));

        String body = """
                {"interestPaid":50,"fee":0,"cashRegisterSessionId":77}
                """;

        mockMvc.perform(post("/pawns/5/extend").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.interestPaid").value(50));
    }

    // ---------- POST /pawns/{id}/redeem ---------------------------------------------------

    @Test
    @DisplayName("POST /pawns/{id}/redeem with a valid body -> 200 with the contract JSON")
    void redeem_validBody_returns200() throws Exception {
        PawnContract contract = mockContract();
        when(pawnService.redeem(eq(5L), any(), eq(STAFF_ID))).thenReturn(contract);
        when(pawnContractMapper.toResponse(any(), any())).thenReturn(sampleResponse());

        String body = """
                {"paidAmount":1100,"cashRegisterSessionId":77}
                """;

        mockMvc.perform(post("/pawns/5/redeem").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    // ---------- POST /pawns/{id}/forfeit --------------------------------------------------

    @Test
    @DisplayName("POST /pawns/{id}/forfeit -> 200 with the contract JSON (no request body)")
    void forfeit_returns200() throws Exception {
        PawnContract contract = mockContract();
        when(pawnService.forfeit(eq(5L), eq(STAFF_ID))).thenReturn(contract);
        when(pawnContractMapper.toResponse(any(), any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/pawns/5/forfeit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }
}
