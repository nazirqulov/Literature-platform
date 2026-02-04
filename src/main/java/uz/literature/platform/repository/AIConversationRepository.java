package uz.literature.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.literature.platform.entity.AIConversation;

import java.util.List;

@Repository
public interface AIConversationRepository extends JpaRepository<AIConversation, Long> {
    
    Page<AIConversation> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    List<AIConversation> findByUserIdAndBookIdOrderByCreatedAtAsc(Long userId, Long bookId);
}
