
package com.polarbookshop.catalogservice.web;

import com.polarbookshop.catalogservice.config.SecurityConfig;
import com.polarbookshop.catalogservice.domain.Book;
import com.polarbookshop.catalogservice.domain.BookAlreadyExistsException;
import com.polarbookshop.catalogservice.domain.BookNotFoundException;
import com.polarbookshop.catalogservice.domain.BookService;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@WebMvcTest(BookController.class)
@Import(SecurityConfig.class)
class BookControllerMvcTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void whenGetBookNotExistingThenShouldReturn404() throws Exception {
        String isbn = "7373731394";

        given(bookService.viewBookDetails(isbn))
                .willThrow(BookNotFoundException.class);

        mockMvc.perform(get("/books/" + isbn))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPostDuplicateBookThenShouldReturn422() throws Exception {
        String isbn = "0306406152";

        given(bookService.addBookToCatalog(any(Book.class)))
                .willThrow(new BookAlreadyExistsException(isbn));

        String bookJson = """
            {
              "isbn": "0306406152",
              "title": "Title",
              "author": "Author",
              "price": 9.90
            }
            """;

        mockMvc.perform(
                post("/books")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_employee")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson)
        )
        .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void whenPostInvalidBookThenShouldReturn400() throws Exception {
        String invalidBookJson = """
            {
              "isbn": "ABC",
              "title": "",
              "author": "Author",
              "price": 9.90
            }
            """;

        mockMvc.perform(
                post("/books")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_employee")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBookJson)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void whenPutExistingBookThenShouldReturn200() throws Exception {
        String isbn = "0306406152";

        given(bookService.existsByIsbn(isbn))
                .willReturn(true);

        given(bookService.editBookDetails(any(), any()))
                .willReturn(
                        Book.of(
                                isbn,
                                "Updated",
                                "Author",
                                12.99,
                                null
                        )
                );

        String bookJson = """
            {
              "isbn": "0306406152",
              "title": "Updated",
              "author": "Author",
              "price": 12.99
            }
            """;

        mockMvc.perform(
                put("/books/" + isbn)
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_employee")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson)
        )
        .andExpect(status().isOk());
    }

    @Test
    void whenPutMissingBookThenShouldReturn201() throws Exception {
        String isbn = "9780306406157";

        given(bookService.existsByIsbn(isbn))
                .willReturn(false);

        given(bookService.editBookDetails(any(), any()))
                .willReturn(
                        Book.of(
                                isbn,
                                "Created",
                                "Author",
                                12.99,
                                null
                        )
                );

        String bookJson = """
            {
              "isbn": "9780306406157",
              "title": "Created",
              "author": "Author",
              "price": 12.99
            }
            """;

        mockMvc.perform(
                put("/books/" + isbn)
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_employee")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson)
        )
        .andExpect(status().isCreated());
    }

    @Test
    void whenPutPathAndBodyIsbnDifferThenShouldReturn400() throws Exception {
        String bookJson = """
            {
              "isbn": "9780306406157",
              "title": "Title",
              "author": "Author",
              "price": 9.90
            }
            """;

        mockMvc.perform(
                put("/books/0306406152")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_employee")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void whenSearchingByAuthorThenReturnMatchingBooks() throws Exception {
        String author = "Lyra Silverstar";

        var book = Book.of(
                "0306406152",
                "Northern Lights",
                author,
                9.90,
                "Polar Publishing"
        );

        given(bookService.searchBooksByAuthor(author))
                .willReturn(List.of(book));

        mockMvc.perform(
                get("/books")
                        .param("author", author)
        )
        .andExpect(status().isOk())
        .andExpect(
                org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .jsonPath("$[0].isbn")
                        .value("0306406152")
        )
        .andExpect(
                org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .jsonPath("$[0].author")
                        .value("Lyra Silverstar")
        );
    }

@Test
void whenDeleteBookWithEmployeeRoleThen204() throws Exception {
    mockMvc.perform(
            delete("/books/7373731394")
                    .with(jwt().authorities(
                            new SimpleGrantedAuthority("ROLE_employee")))
    )
    .andExpect(status().isNoContent());
}

@Test
void whenDeleteBookWithCustomerRoleThen403() throws Exception {
    mockMvc.perform(
            delete("/books/7373731394")
                    .with(jwt().authorities(
                            new SimpleGrantedAuthority("ROLE_customer")))
    )
    .andExpect(status().isForbidden());
}

@Test
void whenDeleteBookNotAuthenticatedThen401() throws Exception {
    mockMvc.perform(delete("/books/7373731394"))
            .andExpect(status().isUnauthorized());
}

}
