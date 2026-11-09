package com.polarbookshop.catalogservice.domain;

import java.util.Optional;

import com.polarbookshop.catalogservice.config.DataConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@Import(DataConfig.class)
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@ActiveProfiles("integration")
class BookRepositoryJdbcTests {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private JdbcAggregateTemplate jdbcAggregateTemplate;

    @Test
    void findBookByIsbnWhenExisting() {
        var isbn = "0306406152";

        var book = Book.of(
                isbn,
                "Title",
                "Author",
                12.90,
                null
        );

        jdbcAggregateTemplate.insert(book);

        Optional<Book> actual = bookRepository.findByIsbn(isbn);

        assertThat(actual).isPresent();
        assertThat(actual.get().isbn()).isEqualTo(isbn);
    }

    @Test
    void findBookByIsbnWhenNotExisting() {
        assertThat(
                bookRepository.findByIsbn("9780306406157")
        ).isEmpty();
    }


    @Test
    void findAllReturnsSavedBooks() {
        bookRepository.save(
                Book.of("0306406152", "First Book", "Author One", 9.90, null)
        );

        bookRepository.save(
                Book.of("9780306406157", "Second Book", "Author Two", 12.90, null)
        );

        var books = bookRepository.findAll();

        assertThat(books)
                .extracting(Book::isbn)
                .contains("0306406152", "9780306406157");
    }

    @Test
    void existsByIsbnWhenExistingReturnsTrue() {
        var book = Book.of(
                "0306406152",
                "Title",
                "Author",
                9.90,
                null
        );

        bookRepository.save(book);

        assertThat(bookRepository.existsByIsbn("0306406152"))
                .isTrue();
    }

    @Test
    void existsByIsbnWhenNotExistingReturnsFalse() {
        assertThat(bookRepository.existsByIsbn("0306406152"))
                .isFalse();
    }

    @Test
    void deleteByIsbnRemovesBook() {
        var book = Book.of(
                "0306406152",
                "Title",
                "Author",
                9.90,
                null
        );

        bookRepository.save(book);

        assertThat(bookRepository.existsByIsbn("0306406152"))
                .isTrue();

        bookRepository.deleteByIsbn("0306406152");

        assertThat(bookRepository.existsByIsbn("0306406152"))
                .isFalse();
    }

    @Test
    void createdDateIsSetWhenBookSaved() {
        var book = Book.of(
                "0306406152",
                "Title",
                "Author",
                9.90,
                null
        );

        var savedBook = bookRepository.save(book);

        assertThat(savedBook.id()).isNotNull();
        assertThat(savedBook.createdDate()).isNotNull();
        assertThat(savedBook.lastModifiedDate()).isNotNull();
    }
    
    @Test
    void findBooksByAuthorReturnsMatchingBooks() {

        var book1 = Book.of(
            "0306406152",
            "Northern Lights",
            "Lyra Silverstar",
            9.90,
            "Polar Publishing"
        );

        var book2 = Book.of(
            "9780306406157",
            "Polar Journey",
            "Iorek Polarson",
            12.90,
            "Arctic Books"
        );

        bookRepository.save(book1);
        bookRepository.save(book2);

        var results = bookRepository.findByAuthor("Lyra Silverstar");

        assertThat(results).extracting(Book::isbn).containsExactly("0306406152");
}

}
