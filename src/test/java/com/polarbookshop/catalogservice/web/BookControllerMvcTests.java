package com.polarbookshop.catalogservice.web;

import com.polarbookshop.catalogservice.domain.BookNotFoundException;
import com.polarbookshop.catalogservice.domain.BookService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.polarbookshop.catalogservice.domain.Book;
import com.polarbookshop.catalogservice.domain.BookAlreadyExistsException;

import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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

    given(bookService.addBookToCatalog(any(Book.class)))
            .willThrow(new BookAlreadyExistsException("1234567890"));

    String bookJson = """
        {
          "isbn": "1234567890",
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
}