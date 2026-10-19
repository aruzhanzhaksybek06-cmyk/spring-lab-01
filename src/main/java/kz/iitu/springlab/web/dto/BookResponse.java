
package kz.iitu.springlab.web.dto;

import kz.iitu.springlab.lab6.domain.Genre;
import java.time.Instant;

public record BookResponse(
        Long id,
        String title,
        String author,
        int year,
        Genre genre,
        Instant createdAt
) {
}

