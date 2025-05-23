package be.ucll.service;

import be.ucll.model.Book;
import be.ucll.model.Magazine;
import be.ucll.model.Publication;
import be.ucll.repository.PublicationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PublicationService {
    private final PublicationRepository publicationRepository;

    public PublicationService(PublicationRepository publicationRepository) {
        this.publicationRepository = publicationRepository;
    }

    public List<Publication> findPublicationsByTitleAndType(String title, String type) {
        List<Publication> result = new ArrayList<>();

        // Case 1: No filters provided, return all publications
        if ((title == null || title.isEmpty()) && (type == null || type.isEmpty())) {
            return publicationRepository.findAll();
        }

        // Case 2: Only title filter provided
        if ((title != null && !title.isEmpty()) && (type == null || type.isEmpty())) {
            result.addAll(publicationRepository.findBooksByTitle(title));
            result.addAll(publicationRepository.findMagazinesByTitle(title));
            return result;
        }

        // Case 3: Only type filter provided
        if ((title == null || title.isEmpty()) && (type != null && !type.isEmpty())) {
            if (type.equalsIgnoreCase("Book")) {
                result.addAll(publicationRepository.findAll().stream()
                        .filter(p -> p instanceof Book)
                        .toList());
            } else if (type.equalsIgnoreCase("Magazine")) {
                result.addAll(publicationRepository.findAll().stream()
                        .filter(p -> p instanceof Magazine)
                        .toList());
            }
            return result;
        }

        // Case 4: Both title and type filters provided
        if (type.equalsIgnoreCase("Book")) {
            result.addAll(publicationRepository.findBooksByTitle(title));
        } else if (type.equalsIgnoreCase("Magazine")) {
            result.addAll(publicationRepository.findMagazinesByTitle(title));
        }

        return result;
    }

    // Method to find publications by available copies
    public List<Publication> findPublicationsByAvailableCopies(int availableCopies) {
        return publicationRepository.findByAvailableCopies(availableCopies);
    }

    public Book addBook(Book book) {
        return publicationRepository.addBook(book);
    }

    public Magazine addMagazine(Magazine magazine) {
        return publicationRepository.addMagazine(magazine);
    }

    public Publication savePublication(Publication publication) {
        if (publication instanceof Book) {
            return publicationRepository.addBook((Book) publication);
        } else if (publication instanceof Magazine) {
            return publicationRepository.addMagazine((Magazine) publication);
        } else {
            throw new IllegalArgumentException("Unsupported publication type: " + publication.getClass().getSimpleName());
        }
    }
}