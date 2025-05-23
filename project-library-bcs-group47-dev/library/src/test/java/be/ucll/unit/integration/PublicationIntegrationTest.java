package be.ucll.unit.integration;

import be.ucll.model.Book;
import be.ucll.model.Magazine;
import be.ucll.model.Publication;
import be.ucll.repository.PublicationRepository;
import be.ucll.service.PublicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PublicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private PublicationRepository publicationRepository;

    @InjectMocks
    private PublicationService publicationService;

    private Book book1;
    private Book book2;
    private Magazine magazine1;
    private Magazine magazine2;

    @BeforeEach
    void setUp() {
        book1 = new Book("Harry Potter", "J.K. Rowling", "978-0-545-01022-1", 1997, 5);
        book2 = new Book("The Hobbit", "J.R.R. Tolkien", "978-0-618-00221-4", 1937, 3);
        magazine1 = new Magazine("National Geographic", "Susan Goldberg", "0027-9358", 2023, 10);
        magazine2 = new Magazine("Time", "Edward Felsenthal", "0040-781X", 2023, 7);
    }

    @Test
    void testGetPublications_NoFilters() throws Exception {
        List<Publication> allPublications = Arrays.asList(book1, book2, magazine1, magazine2);
        when(publicationRepository.findAll()).thenReturn(allPublications);

        mockMvc.perform(get("/publications"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].title", is("Harry Potter")))
                .andExpect(jsonPath("$[1].title", is("The Hobbit")))
                .andExpect(jsonPath("$[2].title", is("National Geographic")))
                .andExpect(jsonPath("$[3].title", is("Time")));

        verify(publicationRepository).findAll();
    }

    @Test
    void testGetPublicationsByAvailableCopies() throws Exception {
        int availableCopies = 5;
        List<Publication> publications = Arrays.asList(book1); // Only book1 has 5 copies
        when(publicationRepository.findByAvailableCopies(availableCopies)).thenReturn(publications);

        mockMvc.perform(get("/publications/stock/{availableCopies}", availableCopies))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1))) // Adjusted to match mock data
                .andExpect(jsonPath("$[0].title", is("Harry Potter")))
                .andExpect(jsonPath("$[0].availableCopies", is(5)));

        verify(publicationRepository).findByAvailableCopies(availableCopies);
    }

    @Test
    void testAddPublication() throws Exception {
        Book newBook = new Book("New Book", "New Author", "978-0-123-45678-9", 2023, 10);
        when(publicationRepository.addBook(any(Book.class))).thenReturn(newBook);

        mockMvc.perform(post("/publications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is("New Book")))
                .andExpect(jsonPath("$.author", is("New Author")))
                .andExpect(jsonPath("$.isbn", is("978-0-123-45678-9")))
                .andExpect(jsonPath("$.publicationYear", is(2023)))
                .andExpect(jsonPath("$.availableCopies", is(10)));

        verify(publicationRepository).addBook(any(Book.class));
    }

    // New test method for querying publications by title and type
    @Test
    void testGetPublicationsByTitleAndType() throws Exception {
        List<Publication> filteredPublications = Arrays.asList(book1); // We expect only "Harry Potter" to match
        when(publicationRepository.findByTitleAndType("Harry Potter", "Book")).thenReturn(filteredPublications);

        mockMvc.perform(get("/publications")
                        .param("title", "Harry Potter")
                        .param("type", "Book"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Harry Potter")))
                .andExpect(jsonPath("$[0].type", is("Book")));

        verify(publicationRepository).findByTitleAndType("Harry Potter", "Book");
    }
}
