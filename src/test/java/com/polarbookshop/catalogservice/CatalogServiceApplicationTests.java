package com.polarbookshop.catalogservice;

import com.polarbookshop.catalogservice.domain.Book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class CatalogServiceApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void whenPostRequestThenBookCreated() {

        var expected = new Book(
                "0306406152",
                "Title",
                "Author",
                9.90
        );

        webTestClient
                .post()
                .uri("/books")
                .bodyValue(expected)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Book.class)
                .value(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual.isbn())
                            .isEqualTo(expected.isbn());
                });
    }

    @Test
void fullBookLifecycleShouldWork() {

    String isbn = "9780306406157";

    var original = new Book(
            isbn,
            "Original Title",
            "Original Author",
            9.90
    );

    var updated = new Book(
            isbn,
            "Updated Title",
            "Updated Author",
            14.90
    );

    // POST - create book
    webTestClient
            .post()
            .uri("/books")
            .bodyValue(original)
            .exchange()
            .expectStatus()
            .isCreated();

    // GET - verify created book
    webTestClient
            .get()
            .uri("/books/" + isbn)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Book.class)
            .value(book ->
                    assertThat(book.title())
                            .isEqualTo("Original Title")
            );

    // PUT - update existing book
    webTestClient
            .put()
            .uri("/books/" + isbn)
            .bodyValue(updated)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Book.class)
            .value(book ->
                    assertThat(book.title())
                            .isEqualTo("Updated Title")
            );

    // DELETE - remove book
    webTestClient
            .delete()
            .uri("/books/" + isbn)
            .exchange()
            .expectStatus()
            .isNoContent();

    // GET again - should now be missing
    webTestClient
            .get()
            .uri("/books/" + isbn)
            .exchange()
            .expectStatus()
            .isNotFound();
    }
}