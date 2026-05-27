package com.example.medistore.service.chat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.model}")
    private String model;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String askAI(String userMessage) {
        try {
            String requestBody = objectMapper.writeValueAsString(
                    new OpenRouterRequest(
                            model,
                            new Message[]{
                                    new Message("system",
                                            "Bạn là AI hỗ trợ khách hàng của nhà thuốc MediStore. " +
                                            "Hãy trả lời ngắn gọn, dễ hiểu, lịch sự. " +
                                            "Không chẩn đoán bệnh chắc chắn. " +
                                            "Nếu triệu chứng nghiêm trọng hoặc kéo dài, hãy khuyên khách hàng gặp bác sĩ/dược sĩ."),
                                    new Message("user", userMessage)
                            }
                    )
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://openrouter.ai/api/v1/chat/completions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("OpenRouter status: " + response.statusCode());
System.out.println("OpenRouter body: " + response.body());

JsonNode root = objectMapper.readTree(response.body());

JsonNode choices = root.path("choices");

if (!choices.isArray() || choices.size() == 0) {
    JsonNode error = root.path("error");

    if (!error.isMissingNode()) {
        return "AI lỗi: " + error.path("message").asText("Không rõ lỗi từ OpenRouter.");
    }

    return "AI chưa trả về nội dung phù hợp. Vui lòng thử lại.";
}

return choices
        .get(0)
        .path("message")
        .path("content")
        .asText("AI chưa có nội dung phản hồi.");

        } catch (Exception e) {
            e.printStackTrace();
            return "Xin lỗi, hiện tại AI chưa phản hồi được. Bạn vui lòng thử lại sau.";
        }
    }

    private record OpenRouterRequest(String model, Message[] messages) {}

    private record Message(String role, String content) {}
}