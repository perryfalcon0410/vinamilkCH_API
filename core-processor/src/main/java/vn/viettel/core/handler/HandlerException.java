package vn.viettel.core.handler;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import vn.viettel.core.util.ErrorMessage;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.exception.*;
import feign.FeignException;
import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import vn.viettel.core.exception.*;

@ControllerAdvice
public class HandlerException {

    Logger logger = LoggerFactory.getLogger(HandlerException.class);

    // handle null body exception.
    @ExceptionHandler(NullBodyException.class)
    public ResponseEntity<?> handleNullBodyException(NullBodyException exception) {
        exception.printStackTrace();
        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.INVALID_BODY);
        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }

    // handle not correct data type in request body [body request]
    @ExceptionHandler(MismatchedInputException.class)
    public ResponseEntity<?> handleMismatchedInputException(MismatchedInputException exception) {
        exception.printStackTrace();
        String errMsg = String.format(ErrorMessage.ERROR_DATATYPE_BODY, exception.getPath().get(0).getFieldName(),
                exception.getTargetType().getSimpleName());

        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.INVALID_BODY.statusCode(), errMsg);
        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }

    // handle field required from request
    @ExceptionHandler(NullFieldException.class)
    public ResponseEntity<?> handleRequiredException(NullFieldException exception) {
        exception.printStackTrace();
        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.VALIDATED_ERROR.statusCode(), exception.getMessage());
        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }

    // handle field empty from request
    @ExceptionHandler(EmptyFieldException.class)
    public ResponseEntity<?> handleEmptyFieldException(EmptyFieldException exception) {
        exception.printStackTrace();
        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.VALIDATED_ERROR.statusCode(), exception.getMessage());
        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }

    // handle field null and blank from request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        exception.printStackTrace();
        BindingResult bindingResult = exception.getBindingResult();
        bindingResult.getFieldError().getDefaultMessage();
        String errorMsg = "'" + bindingResult.getFieldError().getField() + "' "
                + bindingResult.getFieldError().getDefaultMessage();

        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.VALIDATED_ERROR.statusCode(), errorMsg);
        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }

    // handle feign exception
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<?> handleFeignException(FeignException exception) {
        exception.printStackTrace();
        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.NULL_POINTER_EXCEPTION.statusCode(), exception.getMessage());
        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }

    // handle connect exception
    @ExceptionHandler(JDBCConnectionException.class)
    public ResponseEntity<?> handleConnectException(JDBCConnectionException exception) {
        exception.printStackTrace();
        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.CONNECT_DATABASE_FAILED.statusCode(), "Connect database failed.");
        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }

    // handle not correct data type in request parameter [header request]
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        exception.printStackTrace();
        String errMsg = String.format(ErrorMessage.ERROR_DATATYPE_BODY, exception.getName(),
                exception.getRequiredType().getSimpleName());

        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.DATA_TYPE_ERROR.statusCode(), errMsg);
        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }

    // handle request upload not a multipart request
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<?> handleMultipartException(MultipartException exception) {
        exception.printStackTrace();
        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.UNKNOWN.statusCode(), exception.getMessage());
        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException exception) {
        exception.printStackTrace();
        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.DATA_TYPE_ERROR.statusCode(), exception.getMessage());
        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }

    /**
     * process not exists exception
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(ValidateException.class)
    public ResponseHandler handleNotExistsException(ValidateException exception) {
        exception.printStackTrace();
        ResponseHandler response = new ResponseHandler();
        response.setFailure(exception.getMsg());
        return response;
    }

    @ExceptionHandler(UnAuthorizationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseHandler unAuthorizationException(UnAuthorizationException e) {
        e.printStackTrace();
        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.NOT_AUTHORIZED);
        return response;
    }

    @ExceptionHandler(FeatureNotAvailableException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseHandler unavailableFeature(FeatureNotAvailableException e) {
        e.printStackTrace();
        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.COMPANY_FEATURE_NOT_AVAILABLE);
        return response;
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseHandler expiredToken(TokenExpiredException e) {
        e.printStackTrace();
        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.SESSION_EXPIRED);
        return response;
    }

}
