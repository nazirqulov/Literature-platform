package uz.literature.platform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.literature.platform.payload.request.AIQueryRequest;
import uz.literature.platform.payload.response.AIResponse;
import uz.literature.platform.payload.response.BookResponse;
import uz.literature.platform.service.interfaces.AIService;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AIController {
    

    private final AIService aiService;
    
    @PostMapping("/chat")
    public ResponseEntity<AIResponse> chat(@Valid @RequestBody AIQueryRequest request) {
        AIResponse response = aiService.chat(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/analyze/{bookId}")
    public ResponseEntity<AIResponse> analyzeBook(@PathVariable Long bookId) {
        AIResponse response = aiService.analyzeBook(bookId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/summarize/{bookId}")
    public ResponseEntity<AIResponse> summarizeBook(@PathVariable Long bookId) {
        AIResponse response = aiService.summarizeBook(bookId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/recommendations")
    public ResponseEntity<List<BookResponse>> getRecommendations() {
        List<BookResponse> recommendations = aiService.getRecommendations();
        return ResponseEntity.ok(recommendations);
    }
}
