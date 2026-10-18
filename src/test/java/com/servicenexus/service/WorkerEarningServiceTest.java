
package com.servicenexus.service;

import com.servicenexus.model.WorkerEarning;
import com.servicenexus.repository.WorkerEarningRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkerEarningServiceTest {

    @Mock
    private WorkerEarningRepository earningRepository;

    @InjectMocks
    private WorkerEarningService earningService;

    @Test
    void shouldSaveNewEarning() {
        BigDecimal amount = new BigDecimal("500.00");

        when(earningRepository.findByBookingId(101L))
                .thenReturn(Optional.empty());

        when(earningRepository.save(any(WorkerEarning.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WorkerEarning result =
                earningService.recordCompletedBookingEarning(
                        101L, 7L, amount);

        assertEquals(Long.valueOf(101L), result.getBookingId());
        assertEquals(Long.valueOf(7L), result.getWorkerProfileId());
        assertEquals(0, amount.compareTo(result.getAmount()));
        assertNotNull(result.getEarnedAt());

        verify(earningRepository).save(any(WorkerEarning.class));
    }

    @Test
    void shouldRejectInvalidBookingIds() {
        assertThrows(IllegalArgumentException.class,
                () -> earningService.recordCompletedBookingEarning(
                        null, 7L, new BigDecimal("100.00")));

        assertThrows(IllegalArgumentException.class,
                () -> earningService.recordCompletedBookingEarning(
                        0L, 7L, new BigDecimal("100.00")));

        assertThrows(IllegalArgumentException.class,
                () -> earningService.recordCompletedBookingEarning(
                        -1L, 7L, new BigDecimal("100.00")));

        verifyNoInteractions(earningRepository);
    }

    @Test
    void shouldRejectInvalidWorkerProfileIds() {
        assertThrows(IllegalArgumentException.class,
                () -> earningService.recordCompletedBookingEarning(
                        101L, null, new BigDecimal("100.00")));

        assertThrows(IllegalArgumentException.class,
                () -> earningService.recordCompletedBookingEarning(
                        101L, 0L, new BigDecimal("100.00")));

        assertThrows(IllegalArgumentException.class,
                () -> earningService.recordCompletedBookingEarning(
                        101L, -1L, new BigDecimal("100.00")));

        verifyNoInteractions(earningRepository);
    }

    @Test
    void shouldRejectNullOrNegativeAmounts() {
        assertThrows(IllegalArgumentException.class,
                () -> earningService.recordCompletedBookingEarning(
                        101L, 7L, null));

        assertThrows(IllegalArgumentException.class,
                () -> earningService.recordCompletedBookingEarning(
                        101L, 7L, new BigDecimal("-1.00")));

        verifyNoInteractions(earningRepository);
    }

    @Test
    void shouldAllowZeroAmountBecauseServiceAllowsIt() {
        when(earningRepository.findByBookingId(101L))
                .thenReturn(Optional.empty());

        when(earningRepository.save(any(WorkerEarning.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WorkerEarning result =
                earningService.recordCompletedBookingEarning(
                        101L, 7L, BigDecimal.ZERO);

        assertEquals(0, BigDecimal.ZERO.compareTo(result.getAmount()));
        verify(earningRepository).save(any(WorkerEarning.class));
    }

    @Test
    void shouldReturnExistingEarningWhenDetailsMatch() {
        WorkerEarning existing = new WorkerEarning();
        existing.setBookingId(101L);
        existing.setWorkerProfileId(7L);
        existing.setAmount(new BigDecimal("500.00"));

        when(earningRepository.findByBookingId(101L))
                .thenReturn(Optional.of(existing));

        WorkerEarning result =
                earningService.recordCompletedBookingEarning(
                        101L, 7L, new BigDecimal("500.00"));

        assertSame(existing, result);
        verify(earningRepository, never())
                .save(any(WorkerEarning.class));
    }

    @Test
    void shouldRejectDuplicateBookingWithDifferentWorker() {
        WorkerEarning existing = new WorkerEarning();
        existing.setBookingId(101L);
        existing.setWorkerProfileId(7L);
        existing.setAmount(new BigDecimal("500.00"));

        when(earningRepository.findByBookingId(101L))
                .thenReturn(Optional.of(existing));

        assertThrows(IllegalStateException.class,
                () -> earningService.recordCompletedBookingEarning(
                        101L, 8L, new BigDecimal("500.00")));

        verify(earningRepository, never())
                .save(any(WorkerEarning.class));
    }

    @Test
    void shouldRejectDuplicateBookingWithDifferentAmount() {
        WorkerEarning existing = new WorkerEarning();
        existing.setBookingId(101L);
        existing.setWorkerProfileId(7L);
        existing.setAmount(new BigDecimal("500.00"));

        when(earningRepository.findByBookingId(101L))
                .thenReturn(Optional.of(existing));

        assertThrows(IllegalStateException.class,
                () -> earningService.recordCompletedBookingEarning(
                        101L, 7L, new BigDecimal("600.00")));

        verify(earningRepository, never())
                .save(any(WorkerEarning.class));
    }
}
