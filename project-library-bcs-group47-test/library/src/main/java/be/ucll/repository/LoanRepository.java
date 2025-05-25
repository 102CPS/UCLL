package be.ucll.repository;

import be.ucll.model.Loan;
import be.ucll.model.Publication;
import be.ucll.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class LoanRepository {
    private final List<Loan> loans = new ArrayList<>();
    private final UserRepository userRepository;
    private final PublicationRepository publicationRepository;

    public LoanRepository(UserRepository userRepository, PublicationRepository publicationRepository) {
        this.userRepository = userRepository;
        this.publicationRepository = publicationRepository;
    }

    public List<Loan> findAll() {
        return new ArrayList<>(loans);
    }

    public void save(Loan loan) {
        loans.add(loan);
    }

    public List<Loan> findLoansByUser(String email, boolean onlyActive) {
        return loans.stream()
                .filter(loan -> loan.getUser().getEmail().equalsIgnoreCase(email)) // Case-insensitive email check
                .filter(loan -> !onlyActive || (loan.getEndDate() == null || loan.getEndDate().isAfter(LocalDate.now())))
                .collect(Collectors.toList());
    }

    public void deleteByUserEmail(String email) {
        loans.removeIf(loan -> loan.getUser().getEmail().equalsIgnoreCase(email));
    }

    @PostConstruct
    public void initialize() {
        if (userRepository.findAll().isEmpty()) {
            User user1 = new User("John Doe", "password123", "john.doe@example.com", 30);
            User user2 = new User("Jane Smith", "password456", "jane.smith@example.com", 25);
            userRepository.save(user1);
            userRepository.save(user2);
        }

        List<User> users = userRepository.findAll();
        List<Publication> allPublications = publicationRepository.findAll();

        if (!users.isEmpty() && !allPublications.isEmpty()) {
            addLoan(users.get(0), allPublications.get(0), 10, false);

            if (allPublications.size() > 1) {
                addLoan(users.get(0), allPublications.get(1), 30, true); // Returned loan
            }

            if (users.size() > 1 && allPublications.size() > 2) {
                addLoan(users.get(1), allPublications.get(2), 5, false);
            }
        }
    }

    private void addLoan(User user, Publication publication, int daysAgo, boolean returned) {
        List<Publication> publications = new ArrayList<>();
        publications.add(publication);
        Loan loan = new Loan(user, publications, LocalDate.now().minusDays(daysAgo));
        if (returned) loan.returnPublication();
        loans.add(loan);
    }
}