package exception_handler.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@ToString
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(1001, HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误"),
    RESOURCE_NOT_FOUND(1002, HttpStatus.NOT_FOUND, "未找到该资源"),
    REQUEST_VALIDATION_FAILED(1003, HttpStatus.BAD_REQUEST, "请求数据格式验证失败"),
    ;

    private final int code; // 错误码

    private final HttpStatus httpStatus; // HTTP状态码

    private final String message; // 错误信息

}
