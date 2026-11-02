package com.polarbookshop.catalogservice.domain;

public class BookIsbnMismatchException extends RuntimeException {

    public BookIsbnMismatchException(String pathIsbn, String bodyIsbn) {
        super(
            "Path ISBN " + pathIsbn +
            " does not match body ISBN " + bodyIsbn + "."
        );
    }
}