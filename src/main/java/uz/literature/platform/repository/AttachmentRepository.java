package uz.literature.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.literature.platform.entity.Attachment;

import java.util.Optional;

/**
 * Created by: Barkamol
 * DateTime: 2/5/2026 9:18 AM
 */
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    Optional<Attachment> findAttachmentById(Long id);
}
