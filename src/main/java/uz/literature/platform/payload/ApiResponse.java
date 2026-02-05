package uz.literature.platform.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;
import uz.literature.platform.payload.response.ErrorMessageDTO;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {

    private final Boolean success;

    private String message;

    private T content;

    private ApiResponse(Boolean success) {
        this.success = success;
    }

    private ApiResponse(T content, Boolean success) {
        this.content = content;
        this.success = success;
    }

    private ApiResponse(T content, Boolean success, String message) {
        this.content = content;
        this.success = success;
        this.message = message;
    }

    private ApiResponse(String message, Boolean success) {
        this.message = message;
        this.success = success;
    }

    public static <E> ApiResponse<E> success(E data) {
        return new ApiResponse<>(data, Boolean.TRUE);
    }

    public static <E> ApiResponse<E> success(E data, String message) {
        return new ApiResponse<>(data, Boolean.TRUE, message);
    }

    public static <E> ApiResponse<E> success() {
        return new ApiResponse<>(Boolean.TRUE);
    }

    public static ApiResponse<String> success(String message) {
        return new ApiResponse<>(message, Boolean.TRUE);
    }

    public static ApiResponse<ErrorResponse> error(String errorMsg) {
        return new ApiResponse<>(errorMsg, Boolean.FALSE);
    }

    public static ApiResponse<ErrorResponse> error(String errorMsg, Integer errorCode, String errorPath) {
        return new ApiResponse<>(new ErrorResponse(errorMsg, errorCode, errorPath), Boolean.FALSE);
    }

    public static ApiResponse<ErrorMessageDTO> error(String message, int errorCode, String path, Timestamp timestamp) {
        return new ApiResponse<>(new ErrorMessageDTO(timestamp, errorCode, message, path), Boolean.FALSE);
    }

    public static ApiResponse<ErrorResponse> error(ErrorResponse data) {
        return new ApiResponse<>(data, Boolean.FALSE);
    }


    public ApiResponse<T> withContent(Map<String, Object> content) {
        this.content = (T) content;
        return this;
    }

}
