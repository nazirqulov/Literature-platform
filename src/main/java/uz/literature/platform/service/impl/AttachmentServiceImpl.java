package uz.literature.platform.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import uz.literature.platform.entity.Attachment;
import uz.literature.platform.payload.ApiResponse;
import uz.literature.platform.payload.response.AttachmentDTO;
import uz.literature.platform.repository.AttachmentRepository;
import uz.literature.platform.service.interfaces.AttachmentService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by: Barkamol
 * DateTime: 2/5/2026 9:31 AM
 */
@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    @Value("${file.upload-dir}")
    private String baseFolder;

    private final AttachmentRepository attachmentRepository;

    @Transactional
    @Override
    public ApiResponse<?> upload(MultipartFile multipartFile) {
        try {
            if (multipartFile.isEmpty()) {
                return ApiResponse.error("File is empty");
            }

            String contentType = multipartFile.getContentType();

            long size = multipartFile.getSize();
            if (size > 100L * 1024 * 1024) {  // 100MB
                return ApiResponse.error("File is too large");
            }
            if (!Objects.requireNonNull(contentType).startsWith("video/") || !Objects.requireNonNull(contentType).startsWith("image/")) {  // Rasm bo'lishi kerak
                return ApiResponse.error("Invalid file type");
            }

            LocalDate localDate = LocalDate.now();
            int year = localDate.getYear();
            int month = localDate.getMonthValue();
            int day = localDate.getDayOfMonth();

            String originalFilename = multipartFile.getOriginalFilename();
            String name = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(originalFilename);

            Path path = Paths.get(baseFolder)
                    .resolve(String.valueOf(year))
                    .resolve(String.valueOf(month))
                    .resolve(String.valueOf(day));

            Files.createDirectories(path);

            path = path.resolve(name);

            Files.copy(multipartFile.getInputStream(), path);

            Attachment attachment = new Attachment();
            attachment.setFileName(name);
            attachment.setContentType(contentType);
            attachment.setSize(size);
            attachment.setOriginalFileName(originalFilename);
            attachment.setPath(path.toString());

            attachmentRepository.save(attachment);

            return ApiResponse.success(new AttachmentDTO(attachment));
        } catch (IOException e) {
            return ApiResponse.error("Error occurred while uploading attachment: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Resource> downloadById(Integer id) {
        Optional<Attachment> optionalAttachment = attachmentRepository.findAttachmentById(Long.valueOf(id));

        if (optionalAttachment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Attachment attachment = optionalAttachment.get();
        String path = attachment.getPath();
        String contentType = attachment.getContentType();
        long size = attachment.getSize();

        // Faylni yuklash
        Resource resource = new FileSystemResource(path);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // HTTP Headerlarni sozlash
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(size);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getOriginalFileName() + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @Override
    public ApiResponse<?> delete(Integer id) {
        Optional<Attachment> optionalAttachment = attachmentRepository.findAttachmentById(Long.valueOf(id));

        if (optionalAttachment.isEmpty()) {
            return ApiResponse.error("Attachment not found with id: " + id);
        }

        Attachment attachment = optionalAttachment.get();
        Path path = Paths.get(attachment.getPath());
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            return ApiResponse.error("Error occurred while deleting attachment: " + e.getMessage());
        }

        attachmentRepository.delete(attachment);
        return ApiResponse.success("Attachment deleted successfully");
    }
}
