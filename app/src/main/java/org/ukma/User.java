package org.ukma;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
@GenerateBuilder
public class User {
    @NotNull
    private String name;
    @Email
    private String email;
    @MinAge(18)
    private int age;

    public static void main(String[] args) {

    }
}

