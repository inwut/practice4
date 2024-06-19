package org.ukma;

public class Main {
    public static void main(String[] args) throws IllegalAccessException {
        try {
            User user = new UserBuilder()
                    .name("Daria Vetrykush")
                    .email("daria@gmail.com")
                    .age(25)
                    .build();
            Validator.validate(user);

            User invalidUser = new UserBuilder()
                    .name(null)
                    .email("daria@gmail")
                    .age(17)
                    .build();
            Validator.validate(invalidUser);
        } catch (RuntimeException e) {
            System.err.println("Validation failed: " + e.getMessage());
        }
    }
}