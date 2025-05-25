package be.ucll.service;

import be.ucll.model.Book;
import be.ucll.model.Magazine;
import be.ucll.model.Publication;
import be.ucll.repository.PublicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicationService {
    private final PublicationRepository publicationRepository;

    public PublicationService(PublicationRepository publicationRepository) {
        this.publicationRepository = publicationRepository;
    }

    public List<Publication> findPublicationsByTitleAndType(String title, String type) {
        List<Publication> filteredPublications;

        // Filter by title using repository method if provided, else get all
        if (title == null || title.isEmpty()) {
            filteredPublications = publicationRepository.findAll();
        } else {
            filteredPublications = publicationRepository.findByTitleContainingIgnoreCase(title);
        }

        // Filter by type in memory if type filter provided
        if (type == null || type.isEmpty()) {
            return filteredPublications;
        } else if (type.equalsIgnoreCase("Book")) {
            return filteredPublications.stream()
                    .filter(p -> p instanceof Book)
                    .collect(Collectors.toList());
        } else if (type.equalsIgnoreCase("Magazine")) {
            return filteredPublications.stream()
                    .filter(p -> p instanceof Magazine)
                    .collect(Collectors.toList());
        } else {
            return List.of(); // Unsupported type returns empty list
        }
    }

    public List<Publication> findPublicationsByAvailableCopies(int availableCopies) {
        return publicationRepository.findByAvailableCopiesGreaterThanEqual(availableCopies);
    }

    public Book addBook(Book book) {
        return publicationRepository.save(book);
    }

    public Magazine addMagazine(Magazine magazine) {
        return publicationRepository.save(magazine);
    }

    public Publication savePublication(Publication publication) {
        if (publication instanceof Book || publication instanceof Magazine) {
            return publicationRepository.save(publication);
        }
        throw new IllegalArgumentException("Unsupported publication type: " + publication.getClass().getSimpleName());
    }
}
