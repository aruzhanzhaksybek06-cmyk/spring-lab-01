
package kz.iitu.springlab.lab6.web;

import java.net.URI;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import kz.iitu.springlab.lab6.service.BookNotFoundException;
import kz.iitu.springlab.lab6.service.DuplicateTitleException;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(annotations = RestController.class)
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messages;

    public ApiExceptionHandler(MessageSource messages) {
        this.messages = messages;
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ProblemDetail bookNotFound(
            BookNotFoundException exception,
            Locale locale,
            HttpServletRequest request) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                messages.getMessage(
                        "error.bookNotFound",
                        new Object[]{exception.getBookId()},
                        locale)
        );

        problem.setTitle(
                messages.getMessage(
                        "error.notFound.title", null, locale)
        );

        problem.setType(URI.create("urn:problem:book-not-found"));
        problem.setInstance(URI.create(request.getRequestURI()));

        return problem;
    }

    @ExceptionHandler(DuplicateTitleException.class)
    public ProblemDetail duplicateTitle(
            DuplicateTitleException exception,
            HttpServletRequest request) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );

        problem.setTitle("Duplicate title");
        problem.setType(URI.create("urn:problem:duplicate-title"));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("title", exception.getTitle());

        return problem;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Request validation failed"
        );

        problem.setTitle("Validation failed");
        problem.setType(URI.create("urn:problem:validation"));

        if (!ex.getBindingResult().getFieldErrors().isEmpty()) {
            var fieldError = ex.getBindingResult()
                    .getFieldErrors().get(0);

            problem.setProperty("field", fieldError.getField());
            problem.setProperty(
                    "message", fieldError.getDefaultMessage()
            );
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(problem);
    }
}

