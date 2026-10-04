package kz.iitu.springlab.catalog;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<Book> findAll(String author) {
        List<Book> books = repository.findAll();

        if (author == null || author.isBlank()) {
            return books;
        }

        String search = author.toLowerCase();

        return books.stream()
                .filter(book ->
                        book.author().toLowerCase().contains(search))
                .toList();
    }

    public Optional<Book> findById(long id) {
        return repository.findById(id);
    }

    public Book create(Book book) {
        return repository.save(
                new Book(
                        null,
                        book.title(),
                        book.author(),
                        book.year()
                )
        );
    }

    public Optional<Book> replace(long id, Book book) {
        if (repository.findById(id).isEmpty()) {
            return Optional.empty();
        }

        Book updated = repository.save(
                new Book(
                        id,
                        book.title(),
                        book.author(),
                        book.year()
                )
        );

        return Optional.of(updated);
    }

    public boolean delete(long id) {
        return repository.deleteById(id);
    }
}