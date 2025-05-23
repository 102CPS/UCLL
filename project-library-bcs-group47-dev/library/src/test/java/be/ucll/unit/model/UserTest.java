package be.ucll.unit.model;


import be.ucll.model.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void userIsCreatedWithValidInput() {
        User user = new User("Alice", "strongPass1", "alice@example.com", 25);
        assertEquals("Alice", user.getName());
        assertEquals("strongPass1", user.getPassword());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals(25, user.getAge());
    }

    @Test
    void throwsExceptionWhenNameIsEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new User("", "strongPass1", "user@example.com", 25);
        });
        assertEquals("Name is required", exception.getMessage());
    }

    @Test
    void throwsExceptionWhenPasswordIsTooShort() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new User("Bob", "short", "bob@example.com", 30);
        });
        assertEquals("Password must be at least 8 characters long.", exception.getMessage());
    }

    @Test
    void throwsExceptionWhenEmailIsInvalid() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new User("Charlie", "strongPass1", "invalid-email", 30);
        });
        assertEquals("E-mail must be a valid format.", exception.getMessage());
    }

    @Test
    void throwsExceptionWhenAgeIsNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new User("Daisy", "strongPass1", "daisy@example.com", -5);
        });
        assertEquals("Age must be between 0 and 101.", exception.getMessage());
    }

    @Test
    void throwsExceptionWhenAgeIsTooHigh() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new User("Eli", "strongPass1", "eli@example.com", 150);
        });
        assertEquals("Age must be between 0 and 101.", exception.getMessage());
    }
}

