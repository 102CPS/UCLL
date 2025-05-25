package be.ucll.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Password must be at least 8 characters long.")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "E-mail must be a valid format.")
    @Pattern(regexp = ".*@.*\\..*", message = "E-mail must be a valid format.")
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull(message = "Age must be between 0 and 101.")
    @Min(value = 0, message = "Age must be between 0 and 101.")
    @Max(value = 101, message = "Age must be between 0 and 101.")
    @Column(nullable = false)
    private Integer age;

    // Constructor with validation
    public User(String name, String password, String email, int age) {
        setName(name);
        setPassword(password);
        setEmail(email);
        setAge(age);
    }

    // Default constructor (required by JPA)
    public User() {}

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    // Setters with validation (keeping functional validation that can't be done with annotations)
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        this.name = name;
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }
        this.password = password;
    }

    public void setEmail(String email) {
        // Story 15 requirement: Email cannot be changed after creation
        if (this.email != null && !this.email.equals(email)) {
            throw new IllegalArgumentException("Email cannot be changed.");
        }

        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("E-mail must be a valid format.");
        }
        this.email = email;
    }

    public void setAge(int age) {
        if (age < 0 || age > 101) {
            throw new IllegalArgumentException("Age must be between 0 and 101.");
        }
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return email != null && email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }
}