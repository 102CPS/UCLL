package be.ucll.model;

import jakarta.validation.constraints.*;

public class Magazine extends Publication {

    @NotBlank(message = "Editor is required.")
    private String editor;

    @NotBlank(message = "ISSN is required.")
    private String issn;

    // No-args constructor for JSON deserialization
    public Magazine() {
        super();
    }

    public Magazine(String title, String editor, String issn, int publicationYear, int availableCopies) {
        super(title, publicationYear, availableCopies);
        setEditor(editor);
        setIssn(issn);
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
        if (editor == null || editor.trim().isEmpty()) {
            throw new IllegalArgumentException("Editor is required.");
        }
        this.editor = editor;
    }

    public void setIssn(String issn) {
        if (issn == null || issn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISSN is required.");
        }
        this.issn = issn;
    }
}