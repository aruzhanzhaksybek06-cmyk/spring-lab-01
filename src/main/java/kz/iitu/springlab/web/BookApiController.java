
package kz.iitu.springlab.web;

import kz.iitu.springlab.lab6.domain.Book;
import kz.iitu.springlab.lab6.service.BookService;
import kz.iitu.springlab.web.dto.BookMapper;
import kz.iitu.springlab.web.dto.BookRequest;
import kz.iitu.springlab.web.dto.BookResponse;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(
        name = "Books",
        description = "The catalogue of books"
)
@RestController
@RequestMapping("/api/books")
public class BookApiController {

    private final BookService service;
    private final BookMapper mapper;

    public BookApiController(BookService service,
                             BookMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    // Individual Variant 9 - Search books
    @Operation(summary = "Search books by title")
    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam(required = false) String q) {

        if (q == null || q.trim().isEmpty()) {
            ProblemDetail problem =
                    ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

            problem.setTitle("Bad Request");
            problem.setDetail("Search query must not be empty");

            return ResponseEntity
                    .badRequest()
                    .body(problem);
        }

        String query = q.trim().toLowerCase();

        List<BookResponse> results = mapper.toResponseList(
                service.findAll().stream()
                        .filter(book ->
                                book.title().toLowerCase()
                                        .contains(query))
                        .toList()
        );

        return ResponseEntity.ok(results);
    }

    // GET all books - 200
    @GetMapping
    public List<BookResponse> list(
            @RequestParam(required = false) String author) {

        return mapper.toResponseList(
                service.findAll().stream()
                        .filter(book -> author == null ||
                                book.author().equalsIgnoreCase(author))
                        .toList()
        );
    }

    // GET book by ID - 200 or 404
    @Operation(
            summary = "Find a book by its identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The book was found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No book with this id",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public BookResponse find(
            @Parameter(
                    description = "Identifier of the book",
                    example = "42"
            )
            @PathVariable long id) {

        return mapper.toResponse(service.findById(id));
    }

    // POST create book - 201 + Location
    @PostMapping
    public ResponseEntity<BookResponse> create(
            @Valid @RequestBody BookRequest request) {

        Book saved = service.create(
                mapper.toDomain(request)
        );

        return ResponseEntity
                .created(URI.create("/api/books/" + saved.id()))
                .body(mapper.toResponse(saved));
    }

    // PUT update book - 200 or 404
    @PutMapping("/{id}")
    public BookResponse update(
            @PathVariable long id,
            @Valid @RequestBody BookRequest request) {

        Book updated = service.update(
                id, mapper.toDomain(request)
        );

        return mapper.toResponse(updated);
    }

    // DELETE book - 204
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
