package com.automatica.fakenews.service;

import com.automatica.fakenews.model.FakeNewsReport;
import com.automatica.fakenews.model.Status;
import com.automatica.fakenews.repository.FakeNewsReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FakeNewsReportServiceTest {

    @Mock
    private FakeNewsReportRepository reportRepository;

    @InjectMocks
    private FakeNewsReportService reportService;

    @Test
    void testSaveReport_Success() {
        // Given
        FakeNewsReport report = new FakeNewsReport();
        report.setNewsSource("Fake News Daily");
        report.setUrl("http://fakenews.com");
        report.setCategory("Politics");
        report.setDescription("This is a fake news source");

        when(reportRepository.save(any(FakeNewsReport.class))).thenReturn(report);

        // When
        FakeNewsReport savedReport = reportService.saveReport(report);

        // Then
        assertNotNull(savedReport);
        verify(reportRepository, times(1)).save(report);
        assertEquals("Fake News Daily", savedReport.getNewsSource());
    }

    @Test
    void testApproveReport_SetsAllFields() {
        // Given
        FakeNewsReport report = new FakeNewsReport();
        report.setId(1L);
        report.setNewsSource("Fake News Daily");
        report.setUrl("http://fakenews.com");
        report.setCategory("Politics");
        report.setStatus(com.automatica.fakenews.model.Status.PENDING);

        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(reportRepository.save(any(FakeNewsReport.class))).thenReturn(report);

        // When
        reportService.approveReport(1L, "admin");

        // Then
        ArgumentCaptor<FakeNewsReport> captor = ArgumentCaptor.forClass(FakeNewsReport.class);
        verify(reportRepository).save(captor.capture());

        FakeNewsReport savedReport = captor.getValue();
        assertEquals(com.automatica.fakenews.model.Status.APPROVED, savedReport.getStatus(), "Report should be approved");
        assertEquals("admin", savedReport.getApprovedBy(), "Approved by should be set to 'admin'");
        assertNotNull(savedReport.getApprovedAt(), "Approved at timestamp should be set");
    }

    @Test
    void testApproveReport_ReportNotFound_DoesNothing() {
        // Given
        when(reportRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        reportService.approveReport(999L, "admin");

        // Then
        verify(reportRepository, never()).save(any(FakeNewsReport.class));
    }

    @Test
    void testGetApprovedReports_DelegatesToRepository() {
        // Given
        FakeNewsReport report1 = new FakeNewsReport();
        report1.setId(1L);
        report1.setNewsSource("Source 1");
        report1.setStatus(Status.APPROVED);

        FakeNewsReport report2 = new FakeNewsReport();
        report2.setId(2L);
        report2.setNewsSource("Source 2");
        report2.setStatus(Status.APPROVED);

        List<FakeNewsReport> approvedReports = Arrays.asList(report1, report2);
        when(reportRepository.findByStatusOrderByReportedAtDesc(Status.APPROVED)).thenReturn(approvedReports);

        // When
        List<FakeNewsReport> result = reportService.getApprovedReports();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Status.APPROVED, result.get(0).getStatus());
        assertEquals(Status.APPROVED, result.get(1).getStatus());
        verify(reportRepository, times(1)).findByStatusOrderByReportedAtDesc(Status.APPROVED);
    }

    @Test
    void testGetPendingReports_DelegatesToRepository() {
        // Given
        FakeNewsReport report1 = new FakeNewsReport();
        report1.setId(1L);
        report1.setNewsSource("Pending Source 1");
        report1.setStatus(Status.PENDING);

        FakeNewsReport report2 = new FakeNewsReport();
        report2.setId(2L);
        report2.setNewsSource("Pending Source 2");
        report2.setStatus(Status.PENDING);

        List<FakeNewsReport> pendingReports = Arrays.asList(report1, report2);
        when(reportRepository.findByStatusOrderByReportedAtDesc(Status.PENDING)).thenReturn(pendingReports);

        // When
        List<FakeNewsReport> result = reportService.getPendingReports();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Status.PENDING, result.get(0).getStatus());
        assertEquals(Status.PENDING, result.get(1).getStatus());
        verify(reportRepository, times(1)).findByStatusOrderByReportedAtDesc(Status.PENDING);
    }

    @Test
    void testRejectReport_SetsStatusToRejected() {
        // Given
        FakeNewsReport report = new FakeNewsReport();
        report.setId(1L);
        report.setStatus(Status.PENDING);

        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(reportRepository.save(any(FakeNewsReport.class))).thenReturn(report);

        // When
        reportService.rejectReport(1L);

        // Then
        ArgumentCaptor<FakeNewsReport> captor = ArgumentCaptor.forClass(FakeNewsReport.class);
        verify(reportRepository).save(captor.capture());

        FakeNewsReport savedReport = captor.getValue();
        assertEquals(Status.REJECTED, savedReport.getStatus(), "Report should be rejected");
    }
}
