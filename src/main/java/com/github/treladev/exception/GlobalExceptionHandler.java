package com.github.treladev.exception;

import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {


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




}
