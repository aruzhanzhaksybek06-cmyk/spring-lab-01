package kz.iitu.springlab.lab6.domain;

import java.time.Instant;

public record Book(
        long id,
        String title,
        String author,
        int year,
        Genre genre,
        boolean available,
        Instant createdAt
) {
    public Book(
            long id,
            String title,
            String author,
            int year,
            Genre genre,
            boolean available
    ) {
        this(id, title, author, year, genre, available, Instant.now());
    }
}
