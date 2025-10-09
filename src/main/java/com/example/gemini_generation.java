package com.example;

import com.google.genai.Client;
import com.google.genai.ResponseStream;
import com.google.genai.types.*;
import java.util.ArrayList;
import java.util.Arrays;
// Import the thread-safe map implementation
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;

public class gemini_generation {

    // Use ConcurrentHashMap instead of HashMap. This allows multiple threads to safely
    // read and write to the map without interfering with each other.
    private final Map<Long, List<Content>> userHistories = new ConcurrentHashMap<>();

    private final Client client;


    public gemini_generation() {
        // Read the API key from an environment variable
        String apiKey = System.getenv("GEMINI_API_KEY");

        if (apiKey == null) {
            System.err.println("Error: GEMINI_API_KEY environment variable not set.");
            // We can throw an exception to prevent the application from starting without a key
            throw new IllegalStateException("GEMINI_API_KEY environment variable is required.");
        }
        this.client = Client.builder().apiKey(apiKey).build();
    }
    
    public String Generate_content(String prompt, Long chatId) {
        List<Content> currentUserHistory = userHistories.computeIfAbsent(chatId, k -> new ArrayList<>());

        currentUserHistory.add(
            Content.builder()
                .role("user")
                .parts(Arrays.asList(Part.fromText(prompt)))
                .build()
        );

        String model = "gemini-2.5-flash";
        StringBuilder fullResponse = new StringBuilder();

        try {
            List<Tool> tools = new ArrayList<>();
            tools.add(Tool.builder().googleSearch(GoogleSearch.builder().build()).build());

            GenerateContentConfig config = GenerateContentConfig.builder()
                .tools(tools)
                .build();

            ResponseStream<GenerateContentResponse> responseStream = 
                client.models.generateContentStream(model, currentUserHistory, config);

            for (GenerateContentResponse res : responseStream) {
                String text = res.text();
                // System.out.print(text); // Printing can be messy with multiple threads
                fullResponse.append(text);
            }

            // System.out.println("\n");

            currentUserHistory.add(
                Content.builder()
                    .role("model")
                    .parts(Arrays.asList(Part.fromText(fullResponse.toString())))
                    .build()
            );

        } catch (Exception e) {
            System.err.println("Error generating content for chatId " + chatId + ": " + e.getMessage());
            if (!currentUserHistory.isEmpty()) {
                currentUserHistory.remove(currentUserHistory.size() - 1);
            }
            return "Sorry, I encountered an error. Please try again.";
        }
        
        return fullResponse.toString();
    }
}