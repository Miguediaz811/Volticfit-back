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
 * Service for interacting with Google Gemini API models.
 * Uses 2.5-flash-lite for chatbot interactions and 3.1-flash for complex routine generations.
 */
@Service
@Log4j2
public class GeminiService {

    // Para el Chatbot (Dejamos el 2.5 lite que es súper rápido si te lo acepta, o si da problemas usa 1.5-flash)
    private static final String CHAT_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=";

    // Para las Rutinas (Cambiamos al 1.5-flash o 2.0-flash que no fallan con 404 y son unos tanques procesando JSON)
    private static final String ROUTINE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    // Lista de máquinas disponibles en el gimnasio
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
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS) // 2 minutos completos
            .build();
            
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Núcleo compartido para ejecutar las peticiones HTTP hacia la API de Gemini.
     */
    private String executeApiCall(String targetUrl, String prompt) {
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
                .url(targetUrl + apiKey)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Gemini API error ({}): {}", targetUrl, response.code());
                throw new RuntimeException("Error calling Gemini API: " + response.code());
            }

            String responseBody = response.body().string();
            JsonNode root = objectMapper.readTree(responseBody);
            return root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

        } catch (IOException e) {
            log.error("Error calling Gemini endpoint: {}", e.getMessage());
            throw new RuntimeException("Error calling Gemini API", e);
        }
    }

    /**
     * Método genérico de compatibilidad (apunta por defecto al modelo de rutinas)
     */
    public String generate(String prompt) {
        return executeApiCall(ROUTINE_URL, prompt);
    }

    /**
     * Generates a personalized workout routine based on user data using Gemini 3.1 Flash.
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

        return executeApiCall(ROUTINE_URL, prompt);
    }

    /**
     * Generates a generic routine when user has no diagnosis using Gemini 3.1 Flash.
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

        return executeApiCall(ROUTINE_URL, prompt);
    }

    /**
     * Handles a chatbot message about the gym using Gemini 2.5 Flash Lite.
     */
    public String chat(String userMessage) {
        String prompt = String.format("""
                You are a helpful assistant for Volticfit gym.
                Answer questions about gym services, schedules, machines, routines and general fitness advice.
                Keep answers concise and friendly. Respond in the same language as the user.
                If asked something unrelated to fitness or the gym, politely redirect the conversation.

                User message: %s
                """, userMessage);

        return executeApiCall(CHAT_URL, prompt);
    }
}