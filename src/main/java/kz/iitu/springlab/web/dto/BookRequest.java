
package kz.iitu.springlab.web.dto;

import jakarta.validation.constraints.*;
import kz.iitu.springlab.lab6.domain.Genre;

public record BookRequest(
        @NotBlank @Size(max = 120) String title,
        @NotBlank String author,
        @Min(1450) @Max(2100) int year,
        @NotNull Genre genre
) {
}
