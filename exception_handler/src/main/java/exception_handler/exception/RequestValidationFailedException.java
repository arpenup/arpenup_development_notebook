package exception_handler.exception;

import exception_handler.entity.ErrorCode;

import java.util.Map;

public class RequestValidationFailedException   extends MyException {

    public RequestValidationFailedException(Map<String, Object> errorDetail) {
        super(ErrorCode.REQUEST_VALIDATION_FAILED, errorDetail);
    }

    public RequestValidationFailedException(Map<String, Object> errorDetail, Throwable cause) {
        super(ErrorCode.REQUEST_VALIDATION_FAILED, errorDetail, cause);
    }

}
