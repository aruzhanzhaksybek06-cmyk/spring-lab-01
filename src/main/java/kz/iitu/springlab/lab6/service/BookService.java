
package kz.iitu.springlab.lab6.service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import kz.iitu.springlab.lab6.domain.Book;
import kz.iitu.springlab.lab6.domain.Genre;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final ConcurrentHashMap<Long, Book> books =
            new ConcurrentHashMap<>();

    private final AtomicLong sequence = new AtomicLong();

    public BookService() {
        create("The Little Prince",
                "Antoine de Saint-Exupery",
                1943, Genre.FICTION, true);

        create("A Brief History of Time",
                "Stephen Hawking",
                1988, Genre.SCIENCE, true);

        create("Clean Code",
                "Robert Martin",
                2008, Genre.TECHNOLOGY, false);
    }

    // Get all books
    public List<Book> findAll() {
        return books.values().stream()
                .sorted(Comparator.comparingLong(Book::id))
                .toList();
    }

    // Get book by ID
    public Book findById(long id) {
        Book book = books.get(id);

        if (book == null) {
            throw new BookNotFoundException(id);
        }

        return book;
    }

    // Create a new book
    public Book create(String title, String author,
                       int year, Genre genre,
                       boolean available) {

        String cleanTitle = title.strip();
        String cleanAuthor = author.strip();

        // Check for duplicate title and author
        boolean duplicate = books.values().stream()
                .anyMatch(book ->
                        book.title().equalsIgnoreCase(cleanTitle)
                                && book.author().equalsIgnoreCase(cleanAuthor)
                );

        if (duplicate) {
            throw new DuplicateTitleException(cleanTitle);
        }

        long id = sequence.incrementAndGet();

        Book book = new Book(
                id,
                cleanTitle,
                cleanAuthor,
                year,
                genre,
                available
        );

        books.put(id, book);
        return book;
    }

    // Create from a domain object
    public Book create(Book request) {
        return create(
                request.title(),
                request.author(),
                request.year(),
                request.genre(),
                request.available()
        );
    }

    // Update an existing book
    public Book update(long id, Book updated) {
        Book existing = findById(id);

        Book book = new Book(
                id,
                updated.title().strip(),
                updated.author().strip(),
                updated.year(),
                updated.genre(),
                existing.available(),
                existing.createdAt()
        );

        books.put(id, book);
        return book;
    }

    // Delete a book
    public void delete(long id) {
        Book removed = books.remove(id);

        if (removed == null) {
            throw new BookNotFoundException(id);
        }
    }
}
