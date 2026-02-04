package uz.literature.platform.service.interfaces;

import uz.literature.platform.payload.request.AIQueryRequest;
import uz.literature.platform.payload.response.AIResponse;
import uz.literature.platform.payload.response.BookResponse;

import java.util.List;

public interface AIService {
    
    AIResponse chat(AIQueryRequest request);
    
    AIResponse analyzeBook(Long bookId);
    
    List<BookResponse> getRecommendations();
    
    AIResponse summarizeBook(Long bookId);
    
    String extractTextFromPdf(String pdfPath);
}
