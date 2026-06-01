package com.proyecto.volticfit.service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Service for interacting with Google Gemini Flash API.
 * Used for routine generation and chatbot functionality.
 */
@Service
@Log4j2
public class GeminiService {

  // URL base de la API de Gemini Flash, con un placeholder para la clave de API
    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    // Lista de máquinas disponibles en el gimnasio, que se incluirá en los prompts para la generación de rutinas
    private static final String AVAILABLE_MACHINES =
            "- Cinta de correr\n" +
            "- Bicicleta estática\n" +
            "- Mancuernas\n" +
            "- Barra de dominadas\n" +
            "- Máquina de remo\n" +
            "- Banco de pesas\n" +
            "- Máquina de press de piernas\n" +
            "- Máquina de poleas\n" +
            "- Sin máquina";

    @Value("${gemini.api.key}")
    private String apiKey;

private final OkHttpClient httpClient = new OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // Tiempo para conectar
        .writeTimeout(60, TimeUnit.SECONDS)   // Tiempo para enviar datos
        .readTimeout(60, TimeUnit.SECONDS)    // Tiempo para esperar respuesta (¡Este es el clave!)
        .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Sends a prompt to Gemini and returns the text response.
     *
     * @param prompt the prompt to send
     * @return the text response from Gemini
     */
    public String generate(String prompt) {
        String requestBody = """
                {
                  "contents": [
                    {
                      "parts": [
                        { "text": "%s" }
                      ]
                    }
                  ]
                }
                """.formatted(prompt.replace("\"", "\\\"").replace("\n", "\\n"));

        RequestBody body = RequestBody.create(
                requestBody, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(GEMINI_URL + apiKey)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Gemini API error: {}", response.code());
                throw new RuntimeException("Error calling Gemini API: " + response.code());
            }

            String responseBody = response.body().string();
            JsonNode root = objectMapper.readTree(responseBody);
            return root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

        } catch (IOException e) {
            log.error("Error calling Gemini: {}", e.getMessage());
            throw new RuntimeException("Error calling Gemini API");
        }
    }

    /**
     * Generates a personalized workout routine based on user data.
     *
     * @param userContext the user's health and fitness data
     * @return JSON string with the routine
     */
    public String generateRoutine(String userContext) {
        String prompt = String.format("""
                You are a professional fitness trainer. Based on the following user data,
                generate a personalized workout routine.

                User data:
                %s

                IMPORTANT: Use ONLY these available machines (use exact names):
                %s

                Return ONLY a valid JSON object with this exact structure, no extra text:
                {
                  "objetivo": "string",
                  "duracion": "string (e.g. 45 minutos)",
                  "descripcion": "string",
                  "grupo_muscular": "string",
                  "nivel": "string",
                  "ejercicios": [
                    {
                      "nombre": "string",
                      "series": number,
                      "repeticiones": number,
                      "descripcion": "string",
                      "maquina": "string (must be one of the available machines listed above)"
                    }
                  ]
                }
                """, userContext, AVAILABLE_MACHINES);

        return generate(prompt);
    }

    /**
     * Generates a generic routine when user has no diagnosis.
     *
     * @return JSON string with a generic routine
     */
    public String generateGenericRoutine() {
        String prompt = String.format("""
                You are a professional fitness trainer.
                Generate a balanced beginner workout routine.

                IMPORTANT: Use ONLY these available machines (use exact names):
                %s

                Return ONLY a valid JSON object with this exact structure, no extra text:
                {
                  "objetivo": "string",
                  "duracion": "string",
                  "descripcion": "string",
                  "grupo_muscular": "string",
                  "nivel": "Principiante",
                  "ejercicios": [
                    {
                      "nombre": "string",
                      "series": number,
                      "repeticiones": number,
                      "descripcion": "string",
                      "maquina": "string (must be one of the available machines listed above)"
                    }
                  ]
                }
                """, AVAILABLE_MACHINES);

        return generate(prompt);
    }

    /**
     * Handles a chatbot message about the gym.
     *
     * @param userMessage the user's message
     * @return the chatbot response
     */
    public String chat(String userMessage) {
        String prompt = String.format("""
                You are a helpful assistant for Volticfit gym.
                Answer questions about gym services, schedules, machines, routines and general fitness advice.
                Keep answers concise and friendly. Respond in the same language as the user.
                If asked something unrelated to fitness or the gym, politely redirect the conversation.

                User message: %s
                """, userMessage);

        return generate(prompt);
    }
}