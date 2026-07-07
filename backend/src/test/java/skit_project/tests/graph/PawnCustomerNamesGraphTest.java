package skit_project.tests.graph;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.shop.modules.pawn.application.PawnTxQueryService;
import com.volter.shop.modules.pawn.domain.repository.PawnContractRepository;
import com.volter.shop.modules.pawn.domain.repository.PawnTransactionRepository;
import com.volter.shop.modules.pawn.infrastructure.mapper.PawnContractMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Graph coverage — PawnTxQueryService.findCustomerNamesByTransactionIds()")
class PawnCustomerNamesGraphTest {

    @Mock private PawnTransactionRepository pawnTxRepository;
    @Mock private PawnContractRepository contractRepository;
    @Mock private PawnContractMapper pawnContractMapper;
    @Mock private StaffService staffService;

    @InjectMocks private PawnTxQueryService service;

    @Test
    @DisplayName("A — empty input: path [1,2]; covers edge 1->2 and prime path [1,2]")
    void emptyInput_returnsEmpty_withoutQuerying() {
        Map<Long, String> result = service.findCustomerNamesByTransactionIds(List.of());

        assertTrue(result.isEmpty());
        // The guard returns before the repository is ever consulted.
        verify(pawnTxRepository, never()).findCustomerNamesByTransactionIds(anyCollection());
    }

    @Test
    @DisplayName("B — one row: path [1,3,4,5,4,6]; covers du-path [3,4,5], primes [1,3,4,5],[4,5,4],[5,4,6]")
    void oneRow_mapsIdToName() {
        List<Long> ids = List.of(10L);
        when(pawnTxRepository.findCustomerNamesByTransactionIds(ids))
                .thenReturn(List.<Object[]>of(new Object[]{10L, "Ана Ангелова"}));

        Map<Long, String> result = service.findCustomerNamesByTransactionIds(ids);

        assertEquals(1, result.size());
        assertEquals("Ана Ангелова", result.get(10L));
    }

    @Test
    @DisplayName("C — two rows: path [1,3,4,5,4,5,4,6]; covers the second loop traversal, prime [5,4,5]")
    void twoRows_mapsBoth() {
        List<Long> ids = List.of(10L, 20L);
        when(pawnTxRepository.findCustomerNamesByTransactionIds(ids)).thenReturn(List.<Object[]>of(
                new Object[]{10L, "Ана Ангелова"},
                new Object[]{20L, "Борис Петров"}));

        Map<Long, String> result = service.findCustomerNamesByTransactionIds(ids);

        assertEquals(2, result.size());
        assertEquals("Ана Ангелова", result.get(10L));
        assertEquals("Борис Петров", result.get(20L));
    }

    @Test
    @DisplayName("D — non-empty ids but no matching rows: path [1,3,4,6]; covers du-path [3,4,6] and prime [1,3,4,6]")
    void nonEmptyButNoRows_returnsEmptyMap() {
        List<Long> ids = List.of(99L);
        when(pawnTxRepository.findCustomerNamesByTransactionIds(ids)).thenReturn(List.<Object[]>of());

        Map<Long, String> result = service.findCustomerNamesByTransactionIds(ids);

        assertTrue(result.isEmpty());
        verify(pawnTxRepository).findCustomerNamesByTransactionIds(ids);   // the loop ran zero times
    }
}
