package be.ucll.unit.integration;

import be.ucll.model.Book;
import be.ucll.model.Loan;
import be.ucll.model.User;
import be.ucll.repository.LoanRepository;
import be.ucll.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private LoanRepository loanRepository;


    private User user1;
    private User user2;
    private User adultUser;
    private User minorUser;
    private Book book;
    private Loan loan;

    @BeforeEach
    void setUp() {
        user1 = new User("John Doe", "password123", "john.doe@example.com", 30);
        user2 = new User("Jane Smith", "password456", "jane.smith@example.com", 25);
        adultUser = new User("Adult User", "password789", "adult@example.com", 21);
        minorUser = new User("Minor User", "password101", "minor@example.com", 16);

        book = new Book("Test Book", "Test Author", "978-0-545-01022-1", 2020, 5);
        loan = new Loan(user1, List.of(book), LocalDate.now().minusDays(5));
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[1].name", is("Jane Smith")));
    }

    @Test
    void testGetUsersByName() throws Exception {
        when(userRepository.findByNameContainingIgnoreCase("John")).thenReturn(List.of(user1));

        mockMvc.perform(get("/users?name=John"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("John Doe")));
    }

    @Test
    void testGetAllAdultUsers() throws Exception {
        when(userRepository.findByAgeGreaterThanEqual(18)).thenReturn(Arrays.asList(user1, user2, adultUser));

        mockMvc.perform(get("/users/adults"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].age", everyItem(greaterThanOrEqualTo(18))));
    }

    @Test
    void testGetUsersByAgeRange() throws Exception {
        when(userRepository.findByAgeBetween(20, 30)).thenReturn(Arrays.asList(user1, user2, adultUser));

        mockMvc.perform(get("/users/age/{minAge}/{maxAge}", 20, 30))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].age", is(30)))
                .andExpect(jsonPath("$[1].age", is(25)))
                .andExpect(jsonPath("$[2].age", is(21)));
    }

    @Test
    void testGetUserLoans_UserNotFound() throws Exception {
        String email = "nonexistent@example.com";
        when(userRepository.userExists(email)).thenReturn(false);

        mockMvc.perform(get("/users/{email}/loans", email))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", containsString("does not exist")));
    }

    @Test
    void testAddUser() throws Exception {
        User newUser = new User("New User", "newpassword", "new.user@example.com", 28);
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).thenReturn(newUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("New User")))
                .andExpect(jsonPath("$.email", is("new.user@example.com")))
                .andExpect(jsonPath("$.age", is(28)));
    }

    @Test
    void testUpdateUser() throws Exception {
        User updatedUser = new User("Updated User", "updatedpassword", "updated.user@example.com", 30);
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(user1);
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/users/{email}", "john.doe@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Updated User")))
                .andExpect(jsonPath("$.email", is("updated.user@example.com")))
                .andExpect(jsonPath("$.age", is(30)));
    }

    @Test
    void testDeleteUser_WithActiveLoans() throws Exception {
        String email = "john.doe@example.com";
        when(userRepository.findByEmail(email)).thenReturn(user1);
        when(loanRepository.findLoansByUser(email, true)).thenReturn(List.of(loan));

        mockMvc.perform(delete("/users/{email}", email))
                .andExpect(status().isBadRequest());
    }
}
