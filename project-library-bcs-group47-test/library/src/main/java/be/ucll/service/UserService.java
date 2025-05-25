package be.ucll.service;

import be.ucll.model.Loan;
import be.ucll.model.Membership;
import be.ucll.model.User;
import be.ucll.repository.LoanRepository;
import be.ucll.repository.PublicationRepository;
import be.ucll.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final PublicationRepository publicationRepository;

    public UserService(UserRepository userRepository, LoanRepository loanRepository, PublicationRepository publicationRepository) {
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
        this.publicationRepository = publicationRepository;
    }

    public List<User> getUsersByAgeRange(int min, int max) {
        if (min > max) throw new IllegalArgumentException("Minimum age cannot be greater than maximum age.");
        if (min < 0 || max > 150) throw new IllegalArgumentException("Invalid age range. Age must be between 0 and 150.");
        return userRepository.findByAgeBetween(min, max);
    }

    public List<User> getUsersByName(String name) {
        if (name == null || name.isBlank()) {
            return userRepository.findAll();
        }
        List<User> users = userRepository.findByNameContainingIgnoreCase(name);
        if (users.isEmpty()) throw new IllegalArgumentException("No users found with the specified name.");
        return users;
    }

    public List<User> getAllAdultUsers() {
        return userRepository.findByAgeGreaterThanEqual(18);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User addUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists.");
        }
        return userRepository.save(user);
    }

    public String deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist."));

        List<Loan> loans = loanRepository.findByUser(user);

        boolean hasActiveLoans = loans.stream()
                .anyMatch(loan -> loan.getEndDate() == null || !loan.getEndDate().isBefore(LocalDate.now()));

        if (hasActiveLoans) {
            throw new IllegalArgumentException("User has active loans.");
        }

        loanRepository.deleteAll(loans);
        userRepository.delete(user);

        return "User successfully deleted";
    }

    public User updateUser(String email, User updatedUser) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist."));

        // Update only allowed fields
        existingUser.setName(updatedUser.getName());
        existingUser.setPassword(updatedUser.getPassword());
        existingUser.setAge(updatedUser.getAge());

        return userRepository.save(existingUser);
    }





    public User getOldestUser() {
        return userRepository.findOldestUser()
                .orElseThrow(() -> new IllegalArgumentException("No oldest user found."));
    }

    public List<User> getUsersByInterest(String interest) {
        if (interest == null || interest.trim().isEmpty())
            throw new IllegalArgumentException("Interest cannot be empty.");
        List<User> users = userRepository.findUsersByInterest(interest);
        if (users.isEmpty())
            throw new IllegalArgumentException("No users found with interest in " + interest);
        return users;
    }

    public List<User> getUsersByInterestAndAgeSortedByLocation(String interest, int age) {
        if (interest == null || interest.trim().isEmpty())
            throw new IllegalArgumentException("Interest cannot be empty.");
        if (age < 0 || age > 150)
            throw new IllegalArgumentException("Invalid age. Age must be between 0 and 150.");
        List<User> users = userRepository.findUsersByInterestAndAgeSortedByLocation(interest, age);
        if (users.isEmpty())
            throw new IllegalArgumentException("No users found with interest in " + interest + " and older than " + age);
        return users;
    }

    public User addMembership(String email, Membership membership) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist."));

        membership.setUser(user); // make sure this is done

        user.addMembership(membership);

        return userRepository.save(user);
    }

    public Membership getMembershipForDate(String email, LocalDate date) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist."));
        return user.getMemberships().stream()
                .filter(m -> !m.getStartDate().isAfter(date) && !m.getEndDate().isBefore(date))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No membership found for user on date " + date));
    }
}
