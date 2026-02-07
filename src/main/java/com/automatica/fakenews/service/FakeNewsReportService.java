package com.automatica.fakenews.service;

import com.automatica.fakenews.FakeNewsReporterApplication;
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

    @Autowired
    private FakeNewsReportRepository reportRepository;
    private AiIntegrationService aiIntegrationService;

    public FakeNewsReportService(FakeNewsReportRepository reportRepository,AiIntegrationService aiIntegrationService) {
        this.reportRepository = reportRepository;
        this.aiIntegrationService = aiIntegrationService;

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
        }
        FakeNewsReport savedReport = reportRepository.save(report);
        System.out.println("Raport salvat! Începem analiza AI...");

        String textToAnalyze=savedReport.getUrl();
        if(textToAnalyze==null||textToAnalyze.isEmpty()){
            textToAnalyze=savedReport.getDescription();

        }
        try {
            JSONObject aiResult=aiIntegrationService.analyzeText(textToAnalyze);
            if(aiResult==null){
                System.out.println("AI-ul nu a putut analiza textul sau a returnat NULL.");
            }
            else {
                System.out.println(String.format(
                                "E Fake?: %s\n" +
                                "Încredere: %d%%\n" +
                                "Motiv: %s",
                        aiResult.optBoolean("is_fake"),
                        aiResult.optInt("confidence"),
                        aiResult.optString("reason")
                ));
                savedReport.setAiAnalyzed(true);
                savedReport.setAiFake(aiResult.optBoolean("is_fake",false));
                savedReport.setAiReason(aiResult.optString("reason","nici un motiv"));
                savedReport.setAiConfidence(aiResult.optInt("confidence",0));
                return reportRepository.save(savedReport);
            }
        }catch (Exception e){
            System.err.println("Eroare la apelarea AI: " + e.getMessage());
            e.printStackTrace();
        }
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
