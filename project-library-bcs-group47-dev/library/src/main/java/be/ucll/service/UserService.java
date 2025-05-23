package be.ucll.service;

import be.ucll.model.Loan;
import be.ucll.model.User;
import be.ucll.repository.LoanRepository;
import be.ucll.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final LoanRepository loanRepository;

    public UserService(UserRepository userRepository, LoanRepository loanRepository) {
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
    }

    // Story 09 - Retrieve users by age range
    public List<User> getUsersByAgeRange(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Minimum age cannot be greater than maximum age.");
        }
        if (min < 0 || max > 150) {
            throw new IllegalArgumentException("Invalid age range. Age must be between 0 and 150.");
        }
        return userRepository.findByAgeBetween(min, max);
    }

    // Story 10 - Filter users by name (optional parameter)
    public List<User> getUsersByName(String name) {
        if (name == null || name.isBlank()) {
            return userRepository.findAll(); // Return all users if no name is provided
        }

        List<User> users = userRepository.findByNameContainingIgnoreCase(name);
        if (users.isEmpty()) {
            throw new IllegalArgumentException("No users found with the specified name.");
        }
        return users;
    }

    // Story 08 - Retrieve all adult users
    public List<User> getAllAdultUsers() {
        return userRepository.findByAgeGreaterThanEqual(18); // Return users 18 and older
    }

    // Retrieve all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Find user by email
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Add a new user or update existing user
    public User addUser(User user) {
        return userRepository.save(user);  // Save the user and return the saved entity
    }

    // Story 17 - Delete User
    public String deleteUser(String email) {
        // Check if user exists
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User does not exist.");
        }

        // Check if user has active loans
        List<Loan> activeLoans = loanRepository.findLoansByUser(email, true);
        if (!activeLoans.isEmpty()) {
            throw new ActiveLoansException("User has active loans.");
        }

        // Remove inactive loans (all loans since active ones are already filtered out)
        loanRepository.deleteByUserEmail(email);

        // Delete the user
        userRepository.deleteByEmail(email);

        return "User successfully deleted";
    }
}