package be.ucll.unit.service;

import be.ucll.model.Book;
import be.ucll.model.Magazine;
import be.ucll.model.Publication;
import be.ucll.repository.PublicationRepository;
import be.ucll.service.PublicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublicationServiceTest {

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
    void findAllPublications_NoFilters() {
        List<Publication> publications = Arrays.asList(book1, book2, magazine1, magazine2);
        when(publicationRepository.findAll()).thenReturn(publications);

        List<Publication> result = publicationService.findPublicationsByTitleAndType(null, null);
        assertEquals(4, result.size());

        verify(publicationRepository).findAll();
    }

    @Test
    void findPublicationsByTitle_OnlyTitleFilter() {
        // Prepare mock data: only books and magazines containing "Harry"
        Book harryBook = new Book("Harry Potter", "J.K. Rowling", "978-0-545-01022-1", 1997, 5);
        Magazine harryMagazine = new Magazine("Harry Magazine", "Editor", "1234-5678", 2023, 7);
        List<Publication> harryPublications = List.of(harryBook, harryMagazine);

        // Mock the repository method called by the service
        when(publicationRepository.findByTitleContainingIgnoreCase("Harry")).thenReturn(harryPublications);

        // Call service method
        List<Publication> result = publicationService.findPublicationsByTitleAndType("Harry", null);

        // Assertions
        assertEquals(2, result.size()); // 2 because both book and magazine are included

        // Verify the repository method was called
        verify(publicationRepository).findByTitleContainingIgnoreCase("Harry");
    }


    @Test
    void findPublicationsByType_OnlyTypeFilter() {
        List<Publication> publications = Arrays.asList(book1, book2, magazine1, magazine2);
        when(publicationRepository.findAll()).thenReturn(publications);

        List<Publication> books = publicationService.findPublicationsByTitleAndType(null, "Book");
        List<Publication> magazines = publicationService.findPublicationsByTitleAndType(null, "Magazine");

        assertEquals(2, books.size());
        assertEquals(2, magazines.size());

        verify(publicationRepository, times(2)).findAll();
    }

    @Test
    void findPublicationsByTitleAndType_BothFilters() {
        Magazine timeMagazine = new Magazine("Time", "Editor", "0040-781X", 2023, 7);
        List<Publication> publications = List.of(timeMagazine);

        // Mock the repository method the service actually calls
        when(publicationRepository.findByTitleContainingIgnoreCase("Time"))
                .thenReturn(publications);

        List<Publication> result = publicationService.findPublicationsByTitleAndType("Time", "Magazine");
        assertEquals(1, result.size());
        assertEquals("Time", result.get(0).getTitle());

        verify(publicationRepository).findByTitleContainingIgnoreCase("Time");
    }


    @Test
    void findPublicationsByTitle_NoMatch() {
        when(publicationRepository.findByTitleContainingIgnoreCase("Nonexistent"))
                .thenReturn(Collections.emptyList());

        List<Publication> result = publicationService.findPublicationsByTitleAndType("Nonexistent", null);

        assertEquals(0, result.size());

        verify(publicationRepository).findByTitleContainingIgnoreCase("Nonexistent");
        verify(publicationRepository, never()).findAll();
    }

    @Test
    void findPublicationsByType_InvalidType() {
        List<Publication> publications = Arrays.asList(book1, book2, magazine1, magazine2);
        when(publicationRepository.findAll()).thenReturn(publications);

        List<Publication> result = publicationService.findPublicationsByTitleAndType(null, "Newspaper");
        assertEquals(0, result.size());

        verify(publicationRepository).findAll();
    }
}
