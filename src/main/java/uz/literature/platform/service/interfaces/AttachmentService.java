package uz.literature.platform.service.interfaces;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uz.literature.platform.payload.ApiResponse;
import uz.literature.platform.payload.response.AttachmentDTO;

/**
 * Created by: Barkamol
 * DateTime: 2/5/2026 9:29 AM
 */
public interface AttachmentService {


    ApiResponse<?> upload(MultipartFile multipartFile);

    ResponseEntity<Resource> downloadById(Integer id);

    ApiResponse<?> delete(Integer id);
}
