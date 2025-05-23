package be.ucll.model;

public abstract class Publication {
    private String title;
    private int publicationYear;
    private int availableCopies;

    // No-args constructor for JSON deserialization
    protected Publication() {
        // Empty constructor, fields will be set via setters or reflection
    }

    public Publication(String title, int publicationYear, int availableCopies) {
        if (title == null || title.isEmpty()) { // Added null check for safety
            throw new IllegalArgumentException("Title is required");
        }
        if (publicationYear < 0 || publicationYear > 2025) {
            throw new IllegalArgumentException("Publication year must be a positive integer and not in the future.");
        }
        if (availableCopies < 0) {  // Allow 0 copies
            throw new IllegalArgumentException("Available copies cannot be negative.");
        }

        this.title = title;
        this.publicationYear = publicationYear;
        this.availableCopies = availableCopies;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    // Setters
    public void setTitle(String title) { // Added setter for deserialization
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        this.title = title;
    }

    public void setPublicationYear(int publicationYear) { // Added setter for deserialization
        if (publicationYear < 0 || publicationYear > 2025) {
            throw new IllegalArgumentException("Publication year must be a positive integer and not in the future.");
        }
        this.publicationYear = publicationYear;
    }

    public void setAvailableCopies(int availableCopies) {
        if (availableCopies < 0) {  // Allow 0 copies
            throw new IllegalArgumentException("Available copies cannot be negative.");
        }
        this.availableCopies = availableCopies;
    }

    public void lendPublication() {
        if (availableCopies <= 0) {
            throw new IllegalArgumentException("No available copies left for publication.");
        }
        availableCopies--;
    }

    public void returnPublication() {
        this.availableCopies++;
    }
}