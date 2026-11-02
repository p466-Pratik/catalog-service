package com.polarbookshop.catalogservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsbnValidator
        implements ConstraintValidator<ValidIsbn, String> {

    @Override
    public boolean isValid(
            String isbn,
            ConstraintValidatorContext context) {

        if (isbn == null || isbn.isBlank()) {
            return true;
        }

        if (isbn.matches("[0-9]{9}[0-9Xx]")) {
            return isValidIsbn10(isbn);
        }

        if (isbn.matches("[0-9]{13}")) {
            return isValidIsbn13(isbn);
        }

        // Let @Pattern handle malformed lengths/characters.
        return true;
    }

    private boolean isValidIsbn10(String isbn) {

        int sum = 0;

        for (int i = 0; i < 10; i++) {

            char character = isbn.charAt(i);

            int digit;

            if (i == 9 &&
                    (character == 'X' || character == 'x')) {
                digit = 10;
            } else {
                digit = Character.digit(character, 10);
            }

            sum += digit * (10 - i);
        }

        return sum % 11 == 0;
    }

    private boolean isValidIsbn13(String isbn) {

        int sum = 0;

        for (int i = 0; i < 13; i++) {

            int digit =
                    Character.digit(isbn.charAt(i), 10);

            if (i % 2 == 0) {
                sum += digit;
            } else {
                sum += digit * 3;
            }
        }

        return sum % 10 == 0;
    }
}