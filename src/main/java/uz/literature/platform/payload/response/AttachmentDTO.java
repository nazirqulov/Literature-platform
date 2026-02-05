package uz.literature.platform.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.literature.platform.entity.Attachment;

/**
 * Created by: Barkamol
 * DateTime: 2/5/2026 9:30 AM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttachmentDTO {

    private Long id;
    private String fileName;
    private String originalFileName;
    private String contentType;
    private Long size;
    private String path;

    private String url;

    public AttachmentDTO(Attachment attachment) {
        this.id = Long.valueOf(attachment.getId());
        this.fileName = attachment.getFileName();
        this.originalFileName = attachment.getOriginalFileName();
        this.contentType = attachment.getContentType();
        this.size = attachment.getSize();
        this.path = attachment.getPath();
        this.url = "/api/attachment/download/" + attachment.getId();
    }
}
