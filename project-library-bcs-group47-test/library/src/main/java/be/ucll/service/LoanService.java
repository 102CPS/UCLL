package be.ucll.service;

import be.ucll.model.Loan;
import be.ucll.model.Publication;
import be.ucll.model.User;
import be.ucll.model.Membership;
import be.ucll.repository.LoanRepository;
import be.ucll.repository.PublicationRepository;
import be.ucll.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final PublicationRepository publicationRepository;

    public LoanService(LoanRepository loanRepository, UserRepository userRepository, PublicationRepository publicationRepository) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.publicationRepository = publicationRepository;
    }

    public List<Loan> getLoansByUser(String email, boolean onlyActive) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Loan> loans = loanRepository.findByUser(user);
        if (onlyActive) {
            loans = loans.stream()
                    .filter(loan -> loan.getEndDate() == null || !loan.getEndDate().isBefore(LocalDate.now()))
                    .toList();
        }
        return loans;
    }

    public String deleteUserLoans(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Loan> loans = loanRepository.findByUser(user);
        if (loans.isEmpty()) throw new IllegalArgumentException("User has no loans.");
        boolean hasActiveLoans = loans.stream().anyMatch(loan -> loan.getEndDate().isAfter(LocalDate.now()));
        if (hasActiveLoans) throw new IllegalArgumentException("User has active loans.");
        loanRepository.deleteAll(loans);
        return "Loans of user successfully deleted.";
    }

    public Loan registerLoan(String email, LocalDate startDate, List<Long> publicationIds) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (loanRepository.findByUser(user).stream()
                .anyMatch(loan -> loan.getEndDate() == null || !loan.getEndDate().isBefore(LocalDate.now()))) {
            throw new IllegalArgumentException("User already has an active loan.");
        }

        List<Publication> publications = publicationRepository.findAllById(publicationIds);
        if (publications.size() != publicationIds.size()) {
            List<Long> foundIds = publications.stream().map(Publication::getId).toList();
            for (Long id : publicationIds) {
                if (!foundIds.contains(id)) {
                    throw new IllegalArgumentException("Publication with id " + id + " not found.");
                }
            }
        }

        Loan loan = new Loan(user, publications, startDate);
        return loanRepository.save(loan);
    }

    public Loan returnLoan(String email, LocalDate returnDate) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist."));

        Loan loan = loanRepository.findByUser(user).stream()
                .filter(l -> l.getEndDate() == null || !l.getEndDate().isBefore(LocalDate.now()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User has no active loan."));

        if (returnDate == null || returnDate.isBefore(loan.getStartDate())) {
            throw new IllegalArgumentException("Invalid return date. Return date must be after start date.");
        }
        if (returnDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Return date cannot be in the future.");
        }
        if (loan.getEndDate() != null && returnDate.isAfter(loan.getEndDate())) {
            throw new IllegalArgumentException("Loan has already been returned.");
        }

        loan.returnPublication();

        double price = 0.0;
        int numPublications = loan.getPublications().size();
        long totalDays = ChronoUnit.DAYS.between(loan.getStartDate(), returnDate);

        Membership activeMembership = user.getMemberships().stream()
                .filter(m -> !m.getStartDate().isAfter(returnDate) && !m.getEndDate().isBefore(returnDate))
                .findFirst().orElse(null);

        if (activeMembership != null && activeMembership.getRemainingFreeLoans() > 0) {
            activeMembership.redeemFreeLoan();
        } else {
            double pricePerDay = 1.0;
            if (activeMembership != null) {
                switch (activeMembership.getType().toUpperCase()) {
                    case "GOLD": pricePerDay = 0.25; break;
                    case "SILVER": pricePerDay = 0.50; break;
                    case "BRONZE": pricePerDay = 0.75; break;
                }
            }
            price = numPublications * totalDays * pricePerDay;
        }

        // Calculate late return fine
        long daysLate = returnDate.isAfter(loan.getEndDate()) ? ChronoUnit.DAYS.between(loan.getEndDate(), returnDate) : 0;
        double lateFine = daysLate > 0 ? numPublications * daysLate * 0.50 : 0.0;

        price += lateFine;

        loan.setEndDate(returnDate);
        loan.setPrice(price);
        return loanRepository.save(loan);
    }
}
