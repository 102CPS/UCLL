package be.ucll.model;

public class User {
    private Long id;
    private String name;
    private String password;
    private String email;
    private int age;

    // Constructor with validation
    public User(String name, String password, String email, int age) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        this.name = name;

        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }
        this.password = password;

        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("E-mail must be a valid format.");
        }
        this.email = email;

        if (age < 0 || age > 101) {
            throw new IllegalArgumentException("Age must be between 0 and 101.");
        }
        this.age = age;
    }

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

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // Default constructor
    public User() {}
}