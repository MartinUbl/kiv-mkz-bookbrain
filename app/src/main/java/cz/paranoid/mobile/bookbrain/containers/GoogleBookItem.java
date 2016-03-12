package cz.paranoid.mobile.bookbrain.containers;

/**
 * Google books item container
 */
public class GoogleBookItem
{
    /** book name */
    public String name;
    /** book authors string */
    public String authors;
    /** book ISBN */
    public String isbn;

    @Override
    public String toString()
    {
        return name+" ("+authors+")";
    }
}
