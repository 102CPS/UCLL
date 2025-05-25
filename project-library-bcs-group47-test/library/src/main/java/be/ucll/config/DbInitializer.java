package be.ucll.config;

import be.ucll.model.User;
import be.ucll.model.Profile;
import be.ucll.model.Membership;
import be.ucll.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DbInitializer {

    private final UserRepository userRepository;

    public DbInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void initialize() {
        if (userRepository.count() == 0) {
            User user1 = new User("John Doe", "password123", "john.doe@example.com", 30);
            User user2 = new User("Jane Smith", "password456", "jane.smith@example.com", 25);
            User user3 = new User("Bob Johnson", "password789", "bob.johnson@example.com", 45);
            User user4 = new User("Alice Williams", "password101", "alice.williams@example.com", 17);
            User user5 = new User("Charlie Brown", "password202", "charlie.brown@example.com", 22);

            // Adding Memberships with free loans
            Membership m1 = new Membership(LocalDate.now(), LocalDate.now().plusYears(1), "GOLD", 15);
            user1.addMembership(m1);

            Membership m2 = new Membership(LocalDate.now(), LocalDate.now().plusYears(1), "SILVER", 8);
            user2.addMembership(m2);

            Membership m3 = new Membership(LocalDate.now(), LocalDate.now().plusYears(1), "BRONZE", 3);
            user3.addMembership(m3);

            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
            userRepository.save(user4);
            userRepository.save(user5);

            System.out.println("Database initialized with sample users and memberships (with free loans)");
        }
    }
}
