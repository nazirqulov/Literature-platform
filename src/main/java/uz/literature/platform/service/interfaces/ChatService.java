package uz.literature.platform.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.literature.platform.payload.request.ChatMessageRequest;
import uz.literature.platform.payload.response.ChatMessageResponse;
import uz.literature.platform.entity.ChatRoom;

import java.util.List;

public interface ChatService {
    
    ChatMessageResponse sendMessage(ChatMessageRequest request);
    
    Page<ChatMessageResponse> getMessagesByRoom(Long roomId, Pageable pageable);
    
    ChatRoom createRoom(String name, String description, Long bookId);
    
    List<ChatRoom> getAllActiveRooms();
    
    ChatRoom getRoomById(Long id);
}
