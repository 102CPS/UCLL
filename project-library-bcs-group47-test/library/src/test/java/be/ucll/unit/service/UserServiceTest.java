package be.ucll.unit.service;

import be.ucll.model.Loan;
import be.ucll.model.Publication;
import be.ucll.model.User;
import be.ucll.repository.LoanRepository;
import be.ucll.repository.UserRepository;
import be.ucll.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testDeleteUser_Success() {
        String email = "john.doe@example.com";
        User user = new User("John Doe", "password123", email, 30);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(loanRepository.findByUser(user)).thenReturn(Collections.emptyList());

        String result = userService.deleteUser(email);

        assertEquals("User successfully deleted", result);
        verify(userRepository).findByEmail(email);
        verify(loanRepository).findByUser(user);
        verify(loanRepository).deleteAll(Collections.emptyList());
        verify(userRepository).delete(user);
    }

    @Test
    void testDeleteUser_UserDoesNotExist() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(email));
        assertEquals("User does not exist.", exception.getMessage());

        verify(userRepository).findByEmail(email);
        verifyNoMoreInteractions(loanRepository);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testDeleteUser_UserWithActiveLoans() {
        String email = "active.loans@example.com";
        User user = new User("Active Loans User", "password123", email, 25);
        Publication publication = mock(Publication.class);
        Loan activeLoan = new Loan(user, List.of(publication), LocalDate.now().minusDays(10));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(loanRepository.findByUser(user)).thenReturn(List.of(activeLoan));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(email));
        assertEquals("User has active loans.", exception.getMessage());

        verify(userRepository).findByEmail(email);
        verify(loanRepository).findByUser(user);
        verifyNoMoreInteractions(loanRepository);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testDeleteUser_UserWithOnlyInactiveLoans() {
        String email = "inactive.loans@example.com";
        User user = new User("Inactive Loans User", "password123", email, 40);
        Publication publication = mock(Publication.class);
        Loan inactiveLoan = new Loan(user, List.of(publication), LocalDate.now().minusDays(50));
        inactiveLoan.returnPublication();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(loanRepository.findByUser(user)).thenReturn(List.of(inactiveLoan));

        String result = userService.deleteUser(email);

        assertEquals("User successfully deleted", result);
        verify(userRepository).findByEmail(email);
        verify(loanRepository).findByUser(user);
        verify(loanRepository).deleteAll(List.of(inactiveLoan));
        verify(userRepository).delete(user);
    }
}
