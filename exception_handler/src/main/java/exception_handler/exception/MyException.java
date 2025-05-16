package exception_handler.exception;

import exception_handler.entity.ErrorCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class MyException extends RuntimeException {

    private final ErrorCode errorCode; // 错误码

    private Map<String, Object> detail = new HashMap<>(); // 错误详情

    public MyException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public MyException(ErrorCode errorCode, Map<String, Object> detail) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detail = detail;
    }

    public MyException(ErrorCode errorCode, Map<String, Object> detail, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.detail = detail;
    }
}
