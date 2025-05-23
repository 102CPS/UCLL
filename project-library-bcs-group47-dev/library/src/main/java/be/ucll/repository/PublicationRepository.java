package be.ucll.repository;

import be.ucll.model.Book;
import be.ucll.model.Magazine;
import be.ucll.model.Publication;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PublicationRepository {
    public List<Book> books = new ArrayList<>();
    public List<Magazine> magazines = new ArrayList<>();

    public List<Publication> findAll() {
        List<Publication> allPublications = new ArrayList<>();
        allPublications.addAll(books);
        allPublications.addAll(magazines);
        return allPublications;
    }

    public List<Book> findBooksByTitle(String title) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                result.add(book);
            }
        }
        return result;
    }

    public List<Magazine> findMagazinesByTitle(String title) {
        List<Magazine> result = new ArrayList<>();
        for (Magazine magazine : magazines) {
            if (magazine.getTitle().toLowerCase().contains(title.toLowerCase())) {
                result.add(magazine);
            }
        }
        return result;
    }

    // New method to find by title and type
    public List<Publication> findByTitleAndType(String title, String type) {
        List<Publication> result = new ArrayList<>();

        if ("book".equalsIgnoreCase(type)) {
            for (Book book : books) {
                if (book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                    result.add(book);
                }
            }
        } else if ("magazine".equalsIgnoreCase(type)) {
            for (Magazine magazine : magazines) {
                if (magazine.getTitle().toLowerCase().contains(title.toLowerCase())) {
                    result.add(magazine);
                }
            }
        }

        return result;
    }

    public Book addBook(Book book) {
        books.add(book);
        return book;
    }

    public Magazine addMagazine(Magazine magazine) {
        magazines.add(magazine);
        return magazine;
    }

    public List<Publication> findByAvailableCopies(int availableCopies) {
        List<Publication> result = new ArrayList<>();

        for (Book book : books) {
            if (book.getAvailableCopies() >= availableCopies) {
                result.add(book);
            }
        }

        for (Magazine magazine : magazines) {
            if (magazine.getAvailableCopies() >= availableCopies) {
                result.add(magazine);
            }
        }

        return result;
    }

    @PostConstruct
    public void initialize() {
        books.add(new Book("Harry Potter and the Philosopher's Stone", "J.K. Rowling", "978-0-747-53274-3", 1997, 5));
        books.add(new Book("The Lord of the Rings", "J.R.R. Tolkien", "978-0-618-57498-4", 1954, 3));

        magazines.add(new Magazine("National Geographic", "Susan Goldberg", "0027-9358", 2023, 10));
        magazines.add(new Magazine("Time Magazine", "Edward Felsenthal", "0040-781X", 2023, 7));
    }
}
