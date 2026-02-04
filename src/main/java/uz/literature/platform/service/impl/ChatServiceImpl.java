package uz.literature.platform.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.literature.platform.payload.request.ChatMessageRequest;
import uz.literature.platform.payload.response.ChatMessageResponse;
import uz.literature.platform.payload.response.UserResponse;
import uz.literature.platform.entity.Book;
import uz.literature.platform.entity.ChatMessage;
import uz.literature.platform.entity.ChatRoom;
import uz.literature.platform.entity.User;
import uz.literature.platform.exception.ResourceNotFoundException;
import uz.literature.platform.repository.BookRepository;
import uz.literature.platform.repository.ChatMessageRepository;
import uz.literature.platform.repository.ChatRoomRepository;
import uz.literature.platform.service.interfaces.ChatService;
import uz.literature.platform.service.interfaces.UserService;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {
    
    @Autowired
    private ChatMessageRepository messageRepository;
    
    @Autowired
    private ChatRoomRepository roomRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Override
    @Transactional
    public ChatMessageResponse sendMessage(ChatMessageRequest request) {
        User currentUser = userService.getCurrentUserEntity();
        
        ChatRoom room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat xonasi topilmadi"));
        
        ChatMessage message = new ChatMessage();
        message.setRoom(room);
        message.setSender(currentUser);
        message.setContent(request.getContent());
        message.setType(ChatMessage.MessageType.CHAT);
        
        ChatMessage savedMessage = messageRepository.save(message);
        
        return mapToResponse(savedMessage);
    }
    
    @Override
    public Page<ChatMessageResponse> getMessagesByRoom(Long roomId, Pageable pageable) {
        Page<ChatMessage> messages = messageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable);
        return messages.map(this::mapToResponse);
    }
    
    @Override
    @Transactional
    public ChatRoom createRoom(String name, String description, Long bookId) {
        ChatRoom room = new ChatRoom();
        room.setName(name);
        room.setDescription(description);
        room.setIsActive(true);
        
        if (bookId != null) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new ResourceNotFoundException("Kitob topilmadi"));
            room.setBook(book);
        }
        
        return roomRepository.save(room);
    }
    
    @Override
    public List<ChatRoom> getAllActiveRooms() {
        return roomRepository.findByIsActiveTrue();
    }
    
    @Override
    public ChatRoom getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat xonasi topilmadi"));
    }
    
    private ChatMessageResponse mapToResponse(ChatMessage message) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setId(message.getId());
        response.setRoomId(message.getRoom().getId());
        response.setRoomName(message.getRoom().getName());
        response.setContent(message.getContent());
        response.setType(message.getType().name());
        response.setCreatedAt(message.getCreatedAt());
        
        UserResponse userResponse = modelMapper.map(message.getSender(), UserResponse.class);
        userResponse.setRole(message.getSender().getRole().name());
        response.setSender(userResponse);
        
        return response;
    }
}
