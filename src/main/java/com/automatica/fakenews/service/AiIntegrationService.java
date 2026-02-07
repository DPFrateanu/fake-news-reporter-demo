package com.automatica.fakenews.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class AiIntegrationService {

    @Value("${tools.detector.path}")
    private String jarPath;

    @Value("${google.gemini.key}")
    private String apiKey;

    public JSONObject analyzeText(String text) {
        try{
            File jarFILE = new File(jarPath);
            if(!jarFILE.exists()){
                System.err.println("File not found");
            }
            ProcessBuilder processBuilder = new ProcessBuilder("java","-Dfile.encoding=UTF-8","-jar", jarFILE.getAbsolutePath(),text);

            Map<String, String> env = processBuilder.environment();
            env.put("GEMINI_API_KEY", apiKey);

            Process process = processBuilder.start();
            BufferedReader reader= new BufferedReader(
                    new InputStreamReader(process.getInputStream(), java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            process.waitFor();

            String result = output.toString().trim();
            if (result.contains("{")) {
                return new JSONObject(result.substring(result.indexOf("{")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
