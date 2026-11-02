package com.polarbookshop.catalogservice.web;

import com.polarbookshop.catalogservice.domain.Book;
import com.polarbookshop.catalogservice.domain.BookAlreadyExistsException;
import com.polarbookshop.catalogservice.domain.BookNotFoundException;
import com.polarbookshop.catalogservice.domain.BookService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerMvcTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

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
                        new Book(
                                isbn,
                                "Updated",
                                "Author",
                                12.99
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
                        new Book(
                                isbn,
                                "Created",
                                "Author",
                                12.99
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson)
        )
        .andExpect(status().isBadRequest());
    }
}