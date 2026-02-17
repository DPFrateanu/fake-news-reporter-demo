package com.automatica.fakenews.kafka;

import com.automatica.fakenews.model.FakeNewsReport;
import com.automatica.fakenews.repository.FakeNewsReportRepository;
import com.automatica.fakenews.service.AiIntegrationService;
import org.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AiKafkaWorker {

    private final FakeNewsReportRepository reportRepository;
    private final AiIntegrationService aiIntegrationService;

    public AiKafkaWorker(FakeNewsReportRepository reportRepository, AiIntegrationService aiIntegrationService) {
        this.reportRepository = reportRepository;
        this.aiIntegrationService = aiIntegrationService;
    }

    @KafkaListener(topics = "ai-requests", groupId = "ai-worker-group")
    public void consumeReportAnalysisRequest(String reportIdStr){
        System.out.println("Am preluat cererea de la Kafka, o duc spre fake-detector pentru raportul: " + reportIdStr);

        Long reportId = Long.parseLong(reportIdStr);
        Optional<FakeNewsReport> optReport = reportRepository.findById(reportId);
        FakeNewsReport savedReport=optReport.get();

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
                                "Motiv: %s\n" +
                                "Sursa: %s",
                        aiResult.optBoolean("is_fake"),
                        aiResult.optInt("confidence"),
                        aiResult.optString("reason"),
                        aiResult.optString("source")
                ));
                savedReport.setAiAnalyzed(true);
                savedReport.setAiFake(aiResult.optBoolean("is_fake",false));
                savedReport.setAiReason(aiResult.optString("reason","nici un motiv"));
                savedReport.setAiConfidence(aiResult.optInt("confidence",0));
                savedReport.setSource(aiResult.optString("source",""));
                reportRepository.save(savedReport);
                System.out.println("Am salvat raportul in baza de date!");
            }
        }catch (Exception e){
            System.err.println("Eroare la apelarea AI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
