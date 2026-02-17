package com.automatica.fakenews.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AiKafkaProducer {

    private static final String TOPIC = "ai-requests";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public AiKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendReportForAnalysis(Long reportId)
    {
        System.out.println("Am trimis cererea de analiza catre Kafka pentru raportul: " + reportId);
        kafkaTemplate.send(TOPIC, reportId.toString());
    }
}
