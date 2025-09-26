package com.github.treladev.exception;

import org.apache.coyote.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


   @ExceptionHandler(UsernameAlreadyInUseException.class)
    public ResponseEntity<String> handleUsernameAlreadyInUse(UsernameAlreadyInUseException ex){
       return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
   }

   @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFound(UsernameNotFoundException ex){
       return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
   }

   @ExceptionHandler(AdminUpdateForbiddenException.class)
    public ResponseEntity<String> handleAdminUpdateForbidden(AdminUpdateForbiddenException ex){
       return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
   }

   @ExceptionHandler(AdminRoleAssignmentException.class)
    public ResponseEntity<String> handleAdminRoleAssignment(AdminRoleAssignmentException ex){
       return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
   }

   @ExceptionHandler(NoSuchRoleException.class)
    public ResponseEntity<String> handleNoSuchRoleException(NoSuchRoleException ex){
       return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
   }

    @ExceptionHandler(DefaultRoleNotFoundException.class)
    public ResponseEntity<String> handleNoSuchRoleException(DefaultRoleNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ResponseEntity<String> handleRefreshTokenNotFoundException(RefreshTokenNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<String> handleRefreshTokenExpiredException(RefreshTokenExpiredException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }


    @Nullable
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        List<ObjectError> validationErrorsList = ex.getBindingResult().getAllErrors();

        validationErrorsList.forEach( (error) ->
        {
            String fieldName = ((FieldError) error).getField();
            String validationMsg = error.getDefaultMessage();
            validationErrors.put(fieldName,validationMsg);
        });
        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);

    }




}
