package be.ucll.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Magazine extends Publication {

    @NotBlank(message = "Editor is required.")
    private String editor;

    @NotBlank(message = "ISSN is required.")
    private String issn;

    public Magazine() { super(); }

    public Magazine(String title, String editor, String issn, int publicationYear, int availableCopies) {
        super(title, publicationYear, availableCopies);
        setEditor(editor);
        setIssn(issn);
    }

    public String getEditor() { return editor; }
    public String getIssn() { return issn; }

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
