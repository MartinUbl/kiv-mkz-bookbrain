package cz.paranoid.mobile.bookbrain.misc;

import java.util.List;

import cz.paranoid.mobile.bookbrain.containers.GoogleBookItem;

/**
 * Receiver interface for Google Books API client
 */
public interface GoogleBookListListener
{
    /**
     * When book list is retrieved
     * @param bookList  list of items
     */
    void bookListReceived(List<GoogleBookItem> bookList);
}
