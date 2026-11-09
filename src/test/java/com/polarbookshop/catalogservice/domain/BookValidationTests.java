package com.polarbookshop.catalogservice.domain;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookValidationTests {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation
                .buildDefaultValidatorFactory()
                .getValidator();
    }

    @Test
    void whenAllFieldsCorrectThenValidationSucceeds() {
        var book = Book.of(
                "0306406152",
                "Title",
                "Author",
                9.90,
                null
        );

        Set<ConstraintViolation<Book>> violations =
                validator.validate(book);

        assertThat(violations).isEmpty();
    }

    @Test
    void whenIsbnDefinedButIncorrectThenValidationFails() {
        var book = Book.of(
                "a234567890",
                "Title",
                "Author",
                9.90,
                null
        );

        Set<ConstraintViolation<Book>> violations =
                validator.validate(book);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("The ISBN format must be valid.");
    }

    @Test
    void whenTitleBlankThenValidationFails() {
        var book = Book.of(
                "0306406152",
                "",
                "Author",
                9.90,
                null
        );

        var violations = validator.validate(book);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("The book title must be defined.");
    }

    @Test
    void whenAuthorBlankThenValidationFails() {
        var book = Book.of(
                "0306406152",
                "Title",
                "",
                9.90,
                null
        );

        var violations = validator.validate(book);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("The book author must be defined.");
    }

    @Test
    void whenPriceMissingThenValidationFails() {
        var book = Book.of(
                "0306406152",
                "Title",
                "Author",
                null,
                null
        );

        var violations = validator.validate(book);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("The book price must be defined.");
    }

    @Test
    void whenPriceZeroThenValidationFails() {
        var book = Book.of(
                "0306406152",
                "Title",
                "Author",
                0.0,
                null
        );

        var violations = validator.validate(book);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains(
                        "The book price must be greater than zero."
                );
    }

    @Test
    void whenIsbn10CheckDigitValidThenValidationSucceeds() {
        var book = Book.of(
                "0306406152",
                "Title",
                "Author",
                9.90,
                null
        );

        var violations = validator.validate(book);

        assertThat(violations).isEmpty();
    }

    @Test
    void whenIsbn10CheckDigitInvalidThenValidationFails() {
        var book = Book.of(
                "0306406153",
                "Title",
                "Author",
                9.90,
                null
        );

        var violations = validator.validate(book);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains(
                        "The ISBN check digit must be valid."
                );
    }

    @Test
    void whenIsbn13CheckDigitValidThenValidationSucceeds() {
        var book = Book.of(
                "9780306406157",
                "Title",
                "Author",
                9.90,
                null
        );

        var violations = validator.validate(book);

        assertThat(violations).isEmpty();
    }

    @Test
    void whenIsbn13CheckDigitInvalidThenValidationFails() {
        var book = Book.of(
                "9780306406158",
                "Title",
                "Author",
                9.90,
                null
        );

        var violations = validator.validate(book);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains(
                        "The ISBN check digit must be valid."
                );
    }
}