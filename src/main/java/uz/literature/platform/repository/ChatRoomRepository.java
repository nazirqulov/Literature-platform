package uz.literature.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.literature.platform.entity.ChatRoom;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    Optional<ChatRoom> findByName(String name);
    
    List<ChatRoom> findByIsActiveTrue();
    
    Optional<ChatRoom> findByBookId(Long bookId);
}
