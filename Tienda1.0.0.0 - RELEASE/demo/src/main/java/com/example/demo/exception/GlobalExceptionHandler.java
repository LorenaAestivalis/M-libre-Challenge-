package com.example.demo.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice; // Import this
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.example.demo.dto.ErrorResponseDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler { // Malformed JSON in request body
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadableException(
                        HttpMessageNotReadableException ex, HttpServletRequest request) {

                // 👇 Mensaje fijo y claro
                String errorMessage = "JSON MAL FORMADO";

                ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                                LocalDateTime.now(),
                                HttpStatus.BAD_REQUEST.value(), // 400 Bad Request
                                "Bad Request",
                                errorMessage,
                                request.getRequestURI());

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Error para enpoints no mapeadoss
        @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<ErrorResponseDTO> handleNoHandlerFoundException(
                        NoHandlerFoundException ex, HttpServletRequest request) {
                String errorMessage = String.format(
                                "El recurso solicitado '%s %s' no fue encontrado en este servidor.",
                                ex.getHttpMethod(), ex.getRequestURL());

                ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                                LocalDateTime.now(),
                                HttpStatus.NOT_FOUND.value(), // 404 Not Found
                                "Not Found",
                                errorMessage,
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        // Handles ConstraintViolationException (from explicit
        // validator.validate(request))
        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ErrorResponseDTO> handleConstraintViolationException(
                        ConstraintViolationException ex, HttpServletRequest request) {

                Map<String, List<String>> fieldErrors = ex.getConstraintViolations().stream()
                                .collect(Collectors.groupingBy(
                                                violation -> violation.getPropertyPath().toString(),
                                                Collectors.mapping(ConstraintViolation::getMessage,
                                                                Collectors.toList())));

                ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                                LocalDateTime.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request",
                                "*TU*Validation failed for some fields.",
                                request.getRequestURI(),
                                fieldErrors);
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Handles MethodArgumentNotValidException (from @Valid annotation on method
        // parameters)
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(
                        MethodArgumentNotValidException ex, HttpServletRequest request) {

                Map<String, List<String>> fieldErrors = new HashMap<>();
                ex.getBindingResult().getFieldErrors().forEach(error -> {
                        String fieldName = error.getField();
                        String errorMessage = error.getDefaultMessage();
                        fieldErrors.computeIfAbsent(fieldName, k -> new java.util.ArrayList<>()).add(errorMessage);
                });

                ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                                LocalDateTime.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request",
                                "Validation failed for some fields.",
                                request.getRequestURI(),
                                fieldErrors);
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Handles ResourceNotFoundException (when an ID is not found in the data store)
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(
                        ResourceNotFoundException ex, HttpServletRequest request) {

                // Assuming getFieldValue() provides the actual value that was not found, e.g.,
                // an ID.
                // If it returns the resource name, adjust the message accordingly.
                String errorMessage = String.format("Error: El recurso con identificador [%s] no fue encontrado.",
                                ex.getFieldValue());

                ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                                LocalDateTime.now(),
                                HttpStatus.NOT_FOUND.value(),
                                "Not Found",
                                errorMessage,
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        // NEW: Handler for MethodArgumentTypeMismatchException (e.g., non-numeric ID in
        // path)
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ErrorResponseDTO> handleMethodArgumentTypeMismatch(
                        MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

                String paramName = ex.getName();
                String requiredType = Objects.requireNonNull(ex.getRequiredType()).getSimpleName();
                Object invalidValue = ex.getValue();

                String errorMessage = String.format(
                                "El parámetro '%s' debe ser de tipo %s. Valor recibido: '%s'.",
                                paramName, requiredType, invalidValue);

                if ("id".equals(paramName) && "Long".equals(requiredType)) {
                        errorMessage = String.format("Error: El ID debe ser un número válido. Valor recibido: '%s'.",
                                        invalidValue);
                }

                ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                                LocalDateTime.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request",
                                errorMessage,
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Handler for MissingPathVariableException
        @ExceptionHandler(MissingPathVariableException.class)
        public ResponseEntity<ErrorResponseDTO> handleMissingPathVariableException(
                        MissingPathVariableException ex, HttpServletRequest request) {

                String errorMessage = String.format("Error: El %s '%s' es requerido en la URL.", ex.getVariableName(),
                                ex.getVariableName());

                ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                                LocalDateTime.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request",
                                errorMessage,
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Generic Exception handler (catch-all for unexpected errors) - keep this last
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponseDTO> handleGenericException(
                        Exception ex, HttpServletRequest request) {
                ex.printStackTrace(); // Always log the full stack trace for generic errors!

                // Directly use the ErrorResponseDTO constructor
                ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                                LocalDateTime.now(),
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Internal Server Error",
                                "An unexpected internal server error occurred: " + ex.getMessage(),
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
}