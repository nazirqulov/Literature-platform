package uz.literature.platform.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorMessageDTO {

    private Timestamp timestamp;
    private int errorCode;
    private String message;
    private String path;

}
