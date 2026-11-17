
package com.polarbookshop.catalogservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.polarbookshop.catalogservice.domain.Book;
import dasniko.testcontainers.keycloak.KeycloakContainer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("integration")
@Testcontainers
class CatalogServiceApplicationTests {

    @Container
    static KeycloakContainer keycloak =
            new KeycloakContainer("quay.io/keycloak/keycloak:26.0")
                    .withRealmImportFile("test-realm-config.json");

    @DynamicPropertySource
    static void keycloakProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloak.getAuthServerUrl()
                        + "/realms/PolarBookshop"
        );
    }

    private static String isabelleToken;
    private static String bjornToken;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeAll
    static void generateAccessTokens() {
        String authServerUrl = keycloak.getAuthServerUrl();

        if (authServerUrl.endsWith("/")) {
            authServerUrl =
                    authServerUrl.substring(0, authServerUrl.length() - 1);
        }

        WebClient client = WebClient.builder()
                .baseUrl(
                        authServerUrl
                                + "/realms/PolarBookshop"
                                + "/protocol/openid-connect/token"
                )
                .build();

        isabelleToken = tokenFor("isabelle", client);
        bjornToken = tokenFor("bjorn", client);
    }

    private static String tokenFor(String username, WebClient client) {
        JsonNode response = client.post()
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", "polar-test")
                        .with("username", username)
                        .with("password", "password"))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (response == null || !response.has("access_token")) {
            throw new IllegalStateException(
                    "Unable to obtain access token for " + username
            );
        }

        return response.get("access_token").asText();
    }

    // Existing test 1: Create a book

    @Test
    void whenPostRequestThenBookCreated() {
        String isbn = "0306406152";

        var expected = Book.of(
                isbn,
                "Title",
                "Author",
                9.90,
                null
        );

        try {
            webTestClient
                    .post()
                    .uri("/books")
                    .headers(headers ->
                            headers.setBearerAuth(isabelleToken))
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
            webTestClient
                    .delete()
                    .uri("/books/" + isbn)
                    .headers(headers ->
                            headers.setBearerAuth(isabelleToken))
                    .exchange()
                    .expectStatus()
                    .isNoContent();
        }
    }

    // Existing test 2: Full book lifecycle

    @Test
    void fullBookLifecycleShouldWork() {
        String isbn = "9780306406157";

        var original = Book.of(
                isbn,
                "Original Title",
                "Original Author",
                9.90,
                null
        );

        var updated = Book.of(
                isbn,
                "Updated Title",
                "Updated Author",
                14.90,
                null
        );

        // POST: Employee creates book

        webTestClient
                .post()
                .uri("/books")
                .headers(headers ->
                        headers.setBearerAuth(isabelleToken))
                .bodyValue(original)
                .exchange()
                .expectStatus()
                .isCreated();

        // GET: Public browsing is allowed

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

        // PUT: Employee updates book

        webTestClient
                .put()
                .uri("/books/" + isbn)
                .headers(headers ->
                        headers.setBearerAuth(isabelleToken))
                .bodyValue(updated)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Book.class)
                .value(book ->
                        assertThat(book.title())
                                .isEqualTo("Updated Title")
                );

        // DELETE: Employee removes book

        webTestClient
                .delete()
                .uri("/books/" + isbn)
                .headers(headers ->
                        headers.setBearerAuth(isabelleToken))
                .exchange()
                .expectStatus()
                .isNoContent();

        // GET: Book should no longer exist

        webTestClient
                .get()
                .uri("/books/" + isbn)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    // New test 3: Employee can create books

    @Test
    void whenPostAsEmployeeThen201() {
        String isbn = "0132350882";

        var book = Book.of(
                isbn,
                "Security Test Book",
                "Test Author",
                19.90,
                "Test Publisher"
        );

        try {
            webTestClient
                    .post()
                    .uri("/books")
                    .headers(headers ->
                            headers.setBearerAuth(isabelleToken))
                    .bodyValue(book)
                    .exchange()
                    .expectStatus()
                    .isCreated();
        } finally {
            webTestClient
                    .delete()
                    .uri("/books/" + isbn)
                    .headers(headers ->
                            headers.setBearerAuth(isabelleToken))
                    .exchange()
                    .expectStatus()
                    .isNoContent();
        }
    }

    // New test 4: Customer cannot create books

    @Test
    void whenPostAsCustomerThen403() {
        var book = Book.of(
                "0321125215",
                "Customer Test Book",
                "Test Author",
                9.90,
                "Test Publisher"
        );

        webTestClient
                .post()
                .uri("/books")
                .headers(headers ->
                        headers.setBearerAuth(bjornToken))
                .bodyValue(book)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    // New test 5: Unauthenticated users cannot create books

    @Test
    void whenPostUnauthenticatedThen401() {
        var book = Book.of(
                "0201633612",
                "Unauthenticated Test Book",
                "Test Author",
                9.90,
                "Test Publisher"
        );

        webTestClient
                .post()
                .uri("/books")
                .bodyValue(book)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }
}
