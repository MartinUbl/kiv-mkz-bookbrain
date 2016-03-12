package cz.paranoid.mobile.bookbrain.containers;

import java.io.Serializable;

/**
 * Stored book item container
 */
public class StoredItem implements Serializable
{
    /** item id */
    public int id;
    /** book name */
    public String name;
    /** book author */
    public String author;
    /** book ISBN */
    public String isbn;
    /** added date */
    public int addedDate;
    /** custom note */
    public String note;

    /**
     * Stored book item container constructor
     * @param id            item id
     * @param name          book name
     * @param author        book author string
     * @param isbn          book ISBN
     * @param addedDate     added date
     * @param note          custom note
     */
    public StoredItem(int id, String name, String author, String isbn, int addedDate, String note)
    {
        this.id = id;
        this.name = name;
        this.author = author;
        this.isbn = isbn;
        this.addedDate = addedDate;
        this.note = note;
    }
}
