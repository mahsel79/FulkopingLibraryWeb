package se.fulkopinglibraryweb.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Media extends LibraryItem {
    private String id;
    private String title;
    private String type = "Media";
    private String catalog;
    private String director;
    private boolean available;
    private int ReleaseYear;

    @Override
    public String getItemType() {
        return "Media";
    }

    @Override
    public String getItemDetails() {
        return String.format("%s (%s) directed by %s - %d", 
            title, type, director, ReleaseYear);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
