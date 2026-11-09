
package com.polarbookshop.catalogservice;

import com.polarbookshop.catalogservice.domain.Book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("integration")
class CatalogServiceApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void whenPostRequestThenBookCreated() {

        String isbn = "0306406152";

        var expected = Book.of(
                isbn,
                "Title",
                "Author",
                9.90
        );

        try {
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
        } finally {
            // Clean up so other tests can reuse this ISBN.
            webTestClient
                    .delete()
                    .uri("/books/" + isbn)
                    .exchange()
                    .expectStatus()
                    .isNoContent();
        }
    }

    @Test
    void fullBookLifecycleShouldWork() {

        String isbn = "9780306406157";

        var original = Book.of(
                isbn,
                "Original Title",
                "Original Author",
                9.90
        );

        var updated = Book.of(
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

        // GET again - should return 404
        webTestClient
                .get()
                .uri("/books/" + isbn)
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}
