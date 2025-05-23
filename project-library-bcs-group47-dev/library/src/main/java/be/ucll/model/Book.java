package be.ucll.model;

public class Book extends Publication {
    private String author;
    private String isbn;

    public Book(String title, String author, String isbn, int publicationYear, int availableCopies) {
        super(title, publicationYear, availableCopies);  // Call to the superclass constructor
        setAuthor(author);  // Use setter for validation
        setIsbn(isbn);      // Use setter for validation
    }

    // Getters
    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    // Setters with validation
    public void setAuthor(String author) {
        if (author == null || author.isEmpty()) {  // Added null check
            throw new IllegalArgumentException("Author is required");
        }
        this.author = author;
    }

    public void setIsbn(String isbn) {
        if (isbn == null || isbn.isEmpty()) {  // Added null check
            throw new IllegalArgumentException("ISBN is required");
        }
        if (isbn.length() != 17 ||
                isbn.charAt(3) != '-' ||
                isbn.charAt(5) != '-' ||
                isbn.charAt(9) != '-' ||
                isbn.charAt(15) != '-') {
            throw new IllegalArgumentException("ISBN must be in the format 978-0-545-01022-1");
        }
        this.isbn = isbn;
    }
}