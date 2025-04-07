package se.fulkopinglibraryweb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import se.fulkopinglibraryweb.model.Frequency;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import com.google.cloud.firestore.annotation.DocumentId;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Magazine extends LibraryItem {
    @DocumentId
    private String id;
    @NotNull(message = "Publisher cannot be null")
    @NotEmpty(message = "Publisher cannot be empty")
    @Size(min = 2, max = 100, message = "Publisher must be between 2 and 100 characters")
    @Pattern(regexp = "^[\\p{L} \\-'&.,]{2,100}$", message = "Publisher must contain only letters, spaces, and basic punctuation")
    private String publisher;
    private String issn;
    private String category;
    private int issueNumber;
    private String issue;
    private Frequency frequency;
    private int publicationYear;

    public Magazine(String id, String title, boolean available, String issn, String publisher, String issue, Frequency frequency, int publicationYear, String category, int issueNumber) {
        super(title, available);
        this.id = id;
        this.issn = issn;
        this.publisher = publisher;
        this.issue = issue;
        this.frequency = frequency;
        this.publicationYear = publicationYear;
        this.category = category;
        this.issueNumber = issueNumber;
    }

    @Override
    public ItemType getType() {
        return ItemType.MAGAZINE;
    }

    @Override
    public void setType(ItemType type) {
        throw new UnsupportedOperationException("Cannot change type of Magazine");
    }
    
    public void setType(String type) {
        throw new UnsupportedOperationException("Cannot change type of Magazine");
    }

    @Override
    public String getItemDetails() {
        return String.format("%s (%d) - Publisher: %s, Issue: %s, Category: %s", 
            getTitle(), publicationYear, publisher, issue, category);
    }

    @Override
    public String getItemType() {
        return "Magazine";
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getIssn() { return issn; }
    public void setIssn(String issn) { this.issn = issn; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getIssue() { return issue; }
    public void setIssue(String issue) { this.issue = issue; }

    public Frequency getFrequency() { return frequency; }
    public void setFrequency(Frequency frequency) { this.frequency = frequency; }

    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }
}
