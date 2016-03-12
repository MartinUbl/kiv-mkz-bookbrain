package cz.paranoid.mobile.bookbrain.containers;

import java.io.Serializable;

/**
 * Borrowed book item container
 */
public class BorrowedItem implements Serializable
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
    /** limit date for return */
    public long returnDate;
    /** date of last notification */
    public long lastNotifyTime;
    /** custom book note */
    public String note;

    /**
     * Borrowed book constructor
     * @param id            item id
     * @param name          book name
     * @param author        book author
     * @param isbn          book isbn
     * @param addedDate     added date
     * @param returnDate    limit return date
     * @param note          custom note
     */
    public BorrowedItem(int id, String name, String author, String isbn, int addedDate, long returnDate, String note)
    {
        this.id = id;
        this.name = name;
        this.author = author;
        this.isbn = isbn;
        this.addedDate = addedDate;
        this.returnDate = returnDate;
        this.lastNotifyTime = 0;
        this.note = note;
    }
}
