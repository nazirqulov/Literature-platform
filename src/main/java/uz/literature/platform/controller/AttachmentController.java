package uz.literature.platform.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.literature.platform.payload.ApiResponse;
import uz.literature.platform.service.interfaces.AttachmentService;

@RestController
@RequestMapping("/attachment")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Integer id) {

        return attachmentService.downloadById(id);

    }


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> upload(@RequestParam("file") MultipartFile file) {

        ApiResponse<?> result = attachmentService.upload(file);

        return ResponseEntity.ok(result);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Integer id) {

        ApiResponse<?> result = attachmentService.delete(id);

        return ResponseEntity.ok(result);
    }
}
