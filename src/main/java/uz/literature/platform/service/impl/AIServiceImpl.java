package uz.literature.platform.service.impl;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.literature.platform.payload.request.AIQueryRequest;
import uz.literature.platform.payload.response.AIResponse;
import uz.literature.platform.payload.response.BookResponse;
import uz.literature.platform.entity.AIConversation;
import uz.literature.platform.entity.Book;
import uz.literature.platform.entity.User;
import uz.literature.platform.exception.ResourceNotFoundException;
import uz.literature.platform.repository.AIConversationRepository;
import uz.literature.platform.repository.BookRepository;
import uz.literature.platform.repository.ReviewRepository;
import uz.literature.platform.service.interfaces.AIService;
import uz.literature.platform.service.interfaces.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AIServiceImpl implements AIService {
    
    @Autowired
    private OpenAiService openAiService;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private AIConversationRepository conversationRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private UserService userService;
    
    @Value("${openai.model}")
    private String model;
    
    @Override
    @Transactional
    public AIResponse chat(AIQueryRequest request) {
        User currentUser = userService.getCurrentUserEntity();
        
        List<ChatMessage> messages = new ArrayList<>();
        
        // Add context if book is specified
        if (request.getBookId() != null) {
            Book book = bookRepository.findById(request.getBookId())
                    .orElseThrow(() -> new ResourceNotFoundException("Kitob topilmadi"));
            
            String context = String.format(
                "Siz adabiy asosiy assistent sifatida harakat qilyapsiz. " +
                "Hozir foydalanuvchi '%s' nomli kitob haqida gaplashmoqchi. " +
                "Muallif: %s. Tavsif: %s",
                book.getTitle(),
                book.getDescription()
            );
            
            messages.add(new ChatMessage("system", context));
        } else {
            messages.add(new ChatMessage("system", 
                "Siz adabiy assistent sifatida harakat qilyapsiz. " +
                "Kitoblar, mualliflar va adabiyot haqida savollarга javob bering."));
        }
        
        messages.add(new ChatMessage("user", request.getQuery()));
        
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .maxTokens(500)
                .temperature(0.7)
                .build();
        
        ChatCompletionResult result = openAiService.createChatCompletion(completionRequest);
        String response = result.getChoices().get(0).getMessage().getContent();
        
        // Save conversation
        AIConversation conversation = new AIConversation();
        conversation.setUser(currentUser);
        conversation.setUserMessage(request.getQuery());
        conversation.setAiResponse(response);
        conversation.setConversationType(request.getConversationType());
        
        if (request.getBookId() != null) {
            Book book = bookRepository.findById(request.getBookId()).orElse(null);
            conversation.setBook(book);
        }
        
        conversationRepository.save(conversation);
        
        return new AIResponse(response, request.getConversationType(), LocalDateTime.now());
    }
    
    @Override
    public AIResponse analyzeBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Kitob topilmadi"));
        
        String prompt = String.format(
            "Quyidagi kitobni tahlil qiling:\n" +
            "Nomi: %s\n" +
            "Muallif: %s\n" +
            "Tavsif: %s\n\n" +
            "Kitobning asosiy g'oyasi, mavzulari va uslubini tahlil qiling.",
            book.getTitle(),
            book.getDescription()
        );
        
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", "Siz adabiy tanqidchi va tahlilchisiz."));
        messages.add(new ChatMessage("user", prompt));
        
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .maxTokens(800)
                .temperature(0.7)
                .build();
        
        ChatCompletionResult result = openAiService.createChatCompletion(completionRequest);
        String response = result.getChoices().get(0).getMessage().getContent();
        
        return new AIResponse(response, "ANALYSIS", LocalDateTime.now());
    }
    
    @Override
    public List<BookResponse> getRecommendations() {
        User currentUser = userService.getCurrentUserEntity();
        
        // Get user's favorite books and reviews
        List<Book> favoriteBooks = currentUser.getFavorites().stream()
                .map(fav -> fav.getBook())
                .collect(Collectors.toList());
        
        // Simple recommendation: return popular books that user hasn't favorited
        List<Book> allBooks = bookRepository.findByIsActiveTrue(
                org.springframework.data.domain.PageRequest.of(0, 10)
        ).getContent();
        
        return allBooks.stream()
                .filter(book -> !favoriteBooks.contains(book))
                .map(book -> {
                    BookResponse response = new BookResponse();
                    response.setId(book.getId());
                    response.setTitle(book.getTitle());
                    response.setDescription(book.getDescription());
                    response.setCoverImage(book.getCoverImage());
                    response.setAverageRating(book.getAverageRating());
                    return response;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public AIResponse summarizeBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Kitob topilmadi"));
        
        String prompt = String.format(
            "Quyidagi kitobning qisqacha mazmunini yozing:\n" +
            "Nomi: %s\n" +
            "Muallif: %s\n" +
            "Tavsif: %s",
            book.getTitle(),
            book.getDescription()
        );
        
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", "Siz kitoblarni ixcham va aniq tarzda umumlashtiruvchisiz."));
        messages.add(new ChatMessage("user", prompt));
        
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .maxTokens(300)
                .temperature(0.5)
                .build();
        
        ChatCompletionResult result = openAiService.createChatCompletion(completionRequest);
        String response = result.getChoices().get(0).getMessage().getContent();
        
        return new AIResponse(response, "SUMMARY", LocalDateTime.now());
    }
    
    @Override
    public String extractTextFromPdf(String pdfPath) {
        // This would use Apache PDFBox or similar library
        // Implementation depends on requirements
        return "PDF text extraction not yet implemented";
    }
}
