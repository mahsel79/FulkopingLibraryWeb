package se.fulkopinglibraryweb.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public abstract class LibraryItem {
    protected String title;
    private boolean available;
    private ItemType type;

    public LibraryItem() {
    }

    public LibraryItem(String title) {
        this.title = title;
        this.available = true;
    }

    public LibraryItem(String title, boolean available) {
        this.title = title;
        this.available = available;
    }
    
    public abstract String getItemType();
    public abstract String getItemDetails();
    public abstract String getId();
    public abstract void setId(String id);
    
    public ItemType getType() {
        return type;
    }
    
    public void setType(ItemType type) {
        this.type = type;
    }
}
