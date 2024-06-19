package org.ukma;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

public class Validator {
    public static void validate(Object obj) throws IllegalAccessException {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            if(field.isAnnotationPresent(NotNull.class) && field.get(obj) == null) {
                throw new RuntimeException(field.getName() + " can't be null");
            }

            if(field.isAnnotationPresent(Email.class)) {
                String email = field.get(obj).toString();
                if (email != null && !Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$").matcher(email).matches()) {
                    throw new RuntimeException(field.getName() + " is invalid");
                }
            }

            if(field.isAnnotationPresent(MinAge.class)) {
                int age = field.getInt(obj);
                MinAge minAge = field.getAnnotation(MinAge.class);
                if (age < minAge.value()) {
                    throw new RuntimeException(field.getName() + " should be at least " + minAge.value());
                }
            }

        }
    }
}
