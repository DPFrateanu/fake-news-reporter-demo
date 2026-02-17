package com.automatica.fakenews.service;

import com.automatica.fakenews.FakeNewsReporterApplication;
import com.automatica.fakenews.kafka.AiKafkaProducer;
import com.automatica.fakenews.model.FakeNewsReport;
import com.automatica.fakenews.model.Status;
import com.automatica.fakenews.repository.FakeNewsReportRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FakeNewsReportService {

    private final FakeNewsReportRepository reportRepository;
    private final AiKafkaProducer aiKafkaProducer;

    public FakeNewsReportService(FakeNewsReportRepository reportRepository, AiKafkaProducer aiKafkaProducer) {
        this.reportRepository = reportRepository;
        this.aiKafkaProducer = aiKafkaProducer;

    }

    public List<FakeNewsReport> getApprovedReports() {
        return reportRepository.findByStatusOrderByReportedAtDesc(com.automatica.fakenews.model.Status.APPROVED);
    }

    public List<FakeNewsReport> getPendingReports() {
        return reportRepository.findByStatusOrderByReportedAtDesc(com.automatica.fakenews.model.Status.PENDING);
    }

    public List<FakeNewsReport> getRejectedReports() {
        return reportRepository.findByStatusOrderByReportedAtDesc(com.automatica.fakenews.model.Status.REJECTED);
    }

    public List<FakeNewsReport> getAllReports() {
        return reportRepository.findAllByOrderByReportedAtDesc();
    }

    public FakeNewsReport getReportById(Long id) {
        return reportRepository.findById(id).orElse(null);
    }

    @Transactional
    public FakeNewsReport saveReport(FakeNewsReport report) {
        if(report.getId()==null){
            report.setReportedAt(LocalDateTime.now());
            report.setStatus(Status.PENDING);
            report.setAiAnalyzed(false);
        }
        FakeNewsReport savedReport = reportRepository.save(report);
        //trimite spre kafka cererea
        aiKafkaProducer.sendReportForAnalysis(savedReport.getId());

        //returnez instant raportul fara loading screen
        return savedReport;
    }

    @Transactional
    public void approveReport(Long id, String approvedBy) {
        Optional<FakeNewsReport> reportOpt = reportRepository.findById(id);
        if (reportOpt.isPresent()) {
            FakeNewsReport report = reportOpt.get();
            report.setStatus(com.automatica.fakenews.model.Status.APPROVED);
            report.setApprovedAt(LocalDateTime.now());
            report.setApprovedBy(approvedBy);
            reportRepository.save(report);
        }
    }

    @Transactional
    public void rejectReport(Long id, String rejectedBy) {
        Optional<FakeNewsReport> reportOpt = reportRepository.findById(id);
        if (reportOpt.isPresent()) {
            FakeNewsReport report = reportOpt.get();
            report.setStatus(com.automatica.fakenews.model.Status.REJECTED);
            report.setApprovedAt(LocalDateTime.now());
            report.setApprovedBy(rejectedBy);
            reportRepository.save(report);
        }
    }

    @Transactional
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }

}
