package uz.literature.platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uz.literature.platform.payload.request.ChatMessageRequest;
import uz.literature.platform.payload.response.ChatMessageResponse;
import uz.literature.platform.entity.ChatRoom;
import uz.literature.platform.service.interfaces.ChatService;

import java.util.List;

@Controller
public class ChatController {
    
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest request) {
        ChatMessageResponse message = chatService.sendMessage(request);
        messagingTemplate.convertAndSend("/topic/room." + request.getRoomId(), message);
    }
    
    @RestController
    @RequestMapping("/api/chat")
    @CrossOrigin(origins = "*")
    public static class ChatRestController {
        
        @Autowired
        private ChatService chatService;
        
        @GetMapping("/rooms")
        public ResponseEntity<List<ChatRoom>> getAllRooms() {
            List<ChatRoom> rooms = chatService.getAllActiveRooms();
            return ResponseEntity.ok(rooms);
        }
        
        @GetMapping("/rooms/{id}")
        public ResponseEntity<ChatRoom> getRoomById(@PathVariable Long id) {
            ChatRoom room = chatService.getRoomById(id);
            return ResponseEntity.ok(room);
        }
        
        @GetMapping("/rooms/{roomId}/messages")
        public ResponseEntity<Page<ChatMessageResponse>> getRoomMessages(
                @PathVariable Long roomId,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "50") int size) {
            Pageable pageable = PageRequest.of(page, size);
            Page<ChatMessageResponse> messages = chatService.getMessagesByRoom(roomId, pageable);
            return ResponseEntity.ok(messages);
        }
        
        @PostMapping("/rooms")
        public ResponseEntity<ChatRoom> createRoom(
                @RequestParam String name,
                @RequestParam(required = false) String description,
                @RequestParam(required = false) Long bookId) {
            ChatRoom room = chatService.createRoom(name, description, bookId);
            return ResponseEntity.ok(room);
        }
    }
}
