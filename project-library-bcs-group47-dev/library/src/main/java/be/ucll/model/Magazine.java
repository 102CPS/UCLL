package be.ucll.model;

public class Magazine extends Publication {
    private String editor;
    private String issn;

    // No-args constructor for JSON deserialization
    public Magazine() {
        super();
    }

    public Magazine(String title, String editor, String issn, int publicationYear, int availableCopies) {
        super(title, publicationYear, availableCopies);  // Call to the superclass constructor
        setEditor(editor);  // Use setter for validation
        setIssn(issn);      // Use setter for validation
    }

    // Getters
    public String getEditor() {
        return editor;
    }

    public String getIssn() {
        return issn;
    }

    // Setters with validation
    public void setEditor(String editor) {
        if (editor == null || editor.isEmpty()) {  // Added null check
            throw new IllegalArgumentException("Editor is required.");
        }
        this.editor = editor;
    }

    public void setIssn(String issn) {
        if (issn == null || issn.isEmpty()) {  // Added null check
            throw new IllegalArgumentException("ISSN is required.");
        }
        this.issn = issn;
    }
}