package be.ucll.config;

import be.ucll.model.User;
import be.ucll.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DbInitializer {

    private final UserRepository userRepository;

    public DbInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void initialize() {
        // Only initialize if database is empty
        if (userRepository.count() == 0) {
            // Create sample users
            User user1 = new User("John Doe", "password123", "john.doe@example.com", 30);
            User user2 = new User("Jane Smith", "password456", "jane.smith@example.com", 25);
            User user3 = new User("Bob Johnson", "password789", "bob.johnson@example.com", 45);
            User user4 = new User("Alice Williams", "password101", "alice.williams@example.com", 17);
            User user5 = new User("Charlie Brown", "password202", "charlie.brown@example.com", 22);

            // Save users
            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
            userRepository.save(user4);
            userRepository.save(user5);

            System.out.println("Database initialized with sample users");
        }
    }
}