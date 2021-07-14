package vn.viettel.core.handler;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import feign.FeignException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jca.endpoint.GenericMessageEndpointFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import vn.viettel.core.exception.*;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.security.context.SecurityContexHolder;
import vn.viettel.core.util.ErrorMessage;
import vn.viettel.core.util.ResponseMessage;

import javax.persistence.OptimisticLockException;
import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class HandlerException extends ResponseEntityExceptionHandler {

    @Value("${spring.application.name}")
    private String appName;

    @Autowired
    private SecurityContexHolder securityContexHolder;

    /*
     * 400
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        exception.printStackTrace();

        BindingResult bindingResult = exception.getBindingResult();
        bindingResult.getFieldError().getDefaultMessage();
        String errorMsg = "'" + bindingResult.getFieldError().getField() + "' "
                + bindingResult.getFieldError().getDefaultMessage();

        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.VALIDATED_ERROR.statusCode(), errorMsg);
        
        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, ((ServletWebRequest)request).getRequest(), errorMsg);
        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers,
                                                         HttpStatus status, WebRequest request) {
        ex.printStackTrace();

        String errors = "";
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors += error.getField() + ": " + error.getDefaultMessage() + ". ";
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors += error.getObjectName() + ": " + error.getDefaultMessage() + ". ";
        }

        ResponseHandler response = new ResponseHandler();
        response.setFailure(HttpStatus.BAD_REQUEST.value(), errors);

        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, ((ServletWebRequest)request).getRequest(), errors);

        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
                                                        HttpStatus status, WebRequest request) {
        ex.printStackTrace();
        String error = ex.getValue() + " value for " + ex.getPropertyName() + " should be of type "
                + ex.getRequiredType();

        ResponseHandler response = new ResponseHandler();
        response.setFailure(status.value(), error);

        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, ((ServletWebRequest)request).getRequest(), error);

        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        ex.printStackTrace();
        String error = ex.getParameterName() + " parameter is missing";
        ResponseHandler response = new ResponseHandler();
        response.setFailure(status.value(), error);

        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, ((ServletWebRequest)request).getRequest(), error);

        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleAPIBadRequestException(BadRequestException ex, HttpServletRequest request) {
        ex.printStackTrace();

        ResponseHandler response = new ResponseHandler();
        response.setFailure(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, request, ex.getMessage());

        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex,
                                                            WebRequest request) {
        ex.printStackTrace();

        ResponseHandler response = new ResponseHandler();
        response.setFailure(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, ((ServletWebRequest)request).getRequest(), ex.getMessage());

        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    /*
     * 401 UNAUTHORIZED
     * Not login
     */
    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<?> handleAuthenticationException(Exception ex, WebRequest request) {
        ex.printStackTrace();

        ResponseHandler response = new ResponseHandler();
        response.setFailure(HttpStatus.UNAUTHORIZED.value(), "Access Denied");

        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, ((ServletWebRequest)request).getRequest(), ex.getMessage());

        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @ExceptionHandler({javax.naming.AuthenticationException.class})
    public ResponseEntity<?> handleLoginException(Exception ex, WebRequest request) {
        ResponseHandler response = new ResponseHandler();
        response.setFailure(HttpStatus.UNAUTHORIZED.value(), "Access Denied");

        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, ((ServletWebRequest)request).getRequest(), ex.getMessage());

        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    /*
     * 404
     */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex,
                                                                   HttpHeaders headers, HttpStatus status, WebRequest request) {
        ex.printStackTrace();

        String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
        ResponseHandler response = new ResponseHandler();
        response.setFailure(HttpStatus.NOT_FOUND.value(), error);

        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, ((ServletWebRequest)request).getRequest(), error);

        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ex.printStackTrace();

        ResponseHandler response = new ResponseHandler();
        response.setFailure(ex.getStatus().value(), ex.getMessage());

        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, ((ServletWebRequest)request).getRequest(), ex.getMessage());

        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    /*
     * 405
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported( HttpRequestMethodNotSupportedException ex,
                                                                          HttpHeaders headers, HttpStatus status, WebRequest request) {
        ex.printStackTrace();

        ResponseHandler response = new ResponseHandler();
        response.setFailure(HttpStatus.METHOD_NOT_ALLOWED.value(), ex.getMessage());

        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, ((ServletWebRequest)request).getRequest(), ex.getMessage());

        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    /*
     * 415
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                                     HttpHeaders headers, HttpStatus status, WebRequest request) {
        ex.printStackTrace();

        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t + " "));

        ResponseHandler response = new ResponseHandler();
        response.setFailure(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), builder.toString());

        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, ((ServletWebRequest)request).getRequest(), builder.toString());

        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    /*
     * 500
     */
    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        ex.printStackTrace();

        if(ex.getCause() != null && ex.getCause().getClass().isAssignableFrom(ValidateException.class)){
            ResponseHandler response = new ResponseHandler();
            response.setFailure(((ValidateException)ex.getCause()).getStaus(), ((ValidateException)ex.getCause()).getMessage());

            LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, ((ServletWebRequest)request).getRequest(), ((ValidateException)ex.getCause()).getMessage());

            return new ResponseEntity<Object>(response, HttpStatus.OK);
        }

        /*
         * invalid url
         */
        else if(ex.getCause() != null && ex.getCause().getClass().isAssignableFrom(NoClassDefFoundError.class)){
            ResponseHandler response = new ResponseHandler();
            response.setFailure(HttpStatus.NOT_FOUND.value(), request.getDescription(false));

            LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, ((ServletWebRequest)request).getRequest(), request.getDescription(false));

            return new ResponseEntity<Object>(response, HttpStatus.OK);
        }

        ResponseHandler response = new ResponseHandler();
        response.setFailure(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());

        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, ((ServletWebRequest)request).getRequest(), ex.getMessage());

        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<?> handleApplicationException(ApplicationException ex, WebRequest request) {
        ResponseHandler response = new ResponseHandler();
        response.setFailure(ex.getStatus().value(), ex.getMessage());

        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, ((ServletWebRequest)request).getRequest(), ex.getMessage());

        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
                                                             HttpHeaders headers, HttpStatus status, WebRequest request) {
        ex.printStackTrace();

        ResponseHandler response = new ResponseHandler();
        response.setFailure(status.value(), ex.getMessage());

        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, ((ServletWebRequest)request).getRequest(), ex.getMessage());

        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    //////////////////

    /*
     * 403 - FORBIDDEN
     */
    @ExceptionHandler(value = { AccessDeniedException.class })
    public ResponseEntity<?> handleForbiddenException(HttpServletRequest request, AccessDeniedException ex) throws IOException {
        ex.printStackTrace();

        ResponseHandler response = new ResponseHandler();
        response.setFailure(HttpStatus.FORBIDDEN.value(), "Authorization Failed");

        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, request, ex.getMessage());

        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    //404
    @ExceptionHandler(value = { GenericMessageEndpointFactory.InternalResourceException.class })
    public ResponseEntity<?> handleUrlNotFoundException(HttpServletRequest request, GenericMessageEndpointFactory.InternalResourceException ex) throws IOException {
        ex.printStackTrace();

        ResponseHandler response = new ResponseHandler();
        response.setFailure(HttpStatus.NOT_FOUND.value(), "Invalid Url");

        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, request, ex.getMessage());

        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    // handle null body exception.
    @ExceptionHandler(NullBodyException.class)
    public ResponseEntity<?> handleNullBodyException(HttpServletRequest request, NullBodyException exception) {
        exception.printStackTrace();
        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.INVALID_BODY);
        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, request, exception.getMessage());

        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }

    // handle not correct data type in request body [body request]
    @ExceptionHandler(MismatchedInputException.class)
    public ResponseEntity<?> handleMismatchedInputException(HttpServletRequest request, MismatchedInputException exception) {
        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, request, exception.getMessage());
        String errMsg = String.format(ErrorMessage.ERROR_DATATYPE_BODY, exception.getPath().get(0).getFieldName(),
                exception.getTargetType().getSimpleName());

        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.INVALID_BODY.statusCode(), errMsg);

        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, request, errMsg);

        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }

    // handle field required from request
    @ExceptionHandler(NullFieldException.class)
    public ResponseEntity<?> handleRequiredException(HttpServletRequest request, NullFieldException exception) {
        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.VALIDATED_ERROR.statusCode(), exception.getMessage());
        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, request, exception.getMessage());

        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }

    // handle field empty from request
    @ExceptionHandler(EmptyFieldException.class)
    public ResponseEntity<?> handleEmptyFieldException(HttpServletRequest request, EmptyFieldException exception) {
        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.VALIDATED_ERROR.statusCode(), exception.getMessage());
        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, request, exception.getMessage());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // handle field null and blank from request


    // handle feign exception
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<?> handleFeignException(HttpServletRequest request, FeignException exception) {
        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.NULL_POINTER_EXCEPTION.statusCode(), exception.getMessage());
        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, request, exception.getMessage());

        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }

    // handle connect exception
    @ExceptionHandler(JDBCConnectionException.class)
    public ResponseEntity<?> handleConnectException(HttpServletRequest request, JDBCConnectionException exception) {
        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.CONNECT_DATABASE_FAILED.statusCode(), "Connect database failed.");
        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, request, exception.getMessage());

        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }

    // handle not correct data type in request parameter [header request]
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(HttpServletRequest request, MethodArgumentTypeMismatchException exception) {
        String errMsg = String.format(ErrorMessage.ERROR_DATATYPE_BODY, exception.getName(),
                exception.getRequiredType().getSimpleName());

        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.DATA_TYPE_ERROR.statusCode(), errMsg);
        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, request, errMsg);

        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }

    // handle request upload not a multipart request
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<?> handleMultipartException(HttpServletRequest request, MultipartException exception) {
        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.UNKNOWN.statusCode(), exception.getMessage());
        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, request, exception.getMessage());

        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(HttpServletRequest request, NullPointerException exception) {
        ResponseHandler response = new ResponseHandler();
        exception.printStackTrace();
        response.setFailure(ResponseMessage.NULL_POINT.statusCode(), ResponseMessage.NULL_POINT.statusCodeValue());
        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, request, ResponseMessage.NULL_POINT.statusCodeValue());

        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }

    /**
     * process not exists exception
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(ValidateException.class)
    public ResponseEntity<?> handleNotExistsException(HttpServletRequest request, ValidateException exception) {
        ResponseHandler response = new ResponseHandler();
        response.setFailure(exception.getStaus(), exception.getMessage());

        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, request, exception.getMessage());

        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }

    @ExceptionHandler(UnAuthorizationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<?> unAuthorizationException(HttpServletRequest request, UnAuthorizationException exception) {
        exception.printStackTrace();
        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.NOT_AUTHORIZED);
        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, request, exception.getMessage());

        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }

    @ExceptionHandler(FeatureNotAvailableException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> unavailableFeature(HttpServletRequest request, FeatureNotAvailableException exception) {
        exception.printStackTrace();
        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.COMPANY_FEATURE_NOT_AVAILABLE);
        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, request, exception.getMessage());

        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<?> expiredToken(HttpServletRequest request, TokenExpiredException exception) {
        exception.printStackTrace();
        ResponseHandler response = new ResponseHandler();
        response.setFailure(ResponseMessage.SESSION_EXPIRED);
        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, request, exception.getMessage());

        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<?> expiredToken(HttpServletRequest request, OptimisticLockException exception) {
        exception.printStackTrace();
        ResponseHandler response = new ResponseHandler();
        if(exception.getMessage().contains("StockTotal"))
            response.setFailure(ResponseMessage.STORE_WAS_UPDATED_OR_DELETED);
        else response.setFailure(ResponseMessage.ROW_WAS_UPDATED_OR_DELETED);
        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, request, exception.getMessage());

        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }

    /*@ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> callOtherServiceException(HttpServletRequest request, RuntimeException exception) {
        exception.printStackTrace();
        ResponseHandler response = new ResponseHandler();
        String[] args = exception.getMessage().split(":");
        String value = "";
        value = args[0].trim();
        if(args.length > 2)
            value = args[2].trim();
        response.setFailure(ResponseMessage.SERVICE_NOT_START.statusCode(), String.format( ResponseMessage.SERVICE_NOT_START.statusCodeValue(), value));
        LogFile.logToFile(appName, securityContexHolder == null ? "" : securityContexHolder.getContext().getUserName(), LogLevel.ERROR, request, exception.getMessage());

        return new ResponseEntity<ResponseHandler>(response, HttpStatus.OK);
    }*/
}
