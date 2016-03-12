package cz.paranoid.mobile.bookbrain.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

import cz.paranoid.mobile.bookbrain.misc.Constants;
import cz.paranoid.mobile.bookbrain.misc.GoogleBookListListener;
import cz.paranoid.mobile.bookbrain.misc.GoogleBooksClient;
import cz.paranoid.mobile.bookbrain.R;
import cz.paranoid.mobile.bookbrain.containers.GoogleBookItem;

/**
 * Abstract activity for all Add activities for books
 */
public abstract class AddBookAbstractActivity extends AppCompatActivity implements GoogleBookListListener
{
    /** stored scanned ISBN */
    protected String scannedISBN = "";
    /** are we searching using name? */
    protected boolean nameSearchMode = false;
    /** stored reference to progress dialog shown */
    private ProgressDialog shownProgressDialog = null;

    /**
     * Scan button was clicked
     * @param view      source view
     */
    public void scanButtonClicked(View view)
    {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    /**
     * Shows loading spinner
     */
    public void showBookLoading()
    {
        shownProgressDialog = ProgressDialog.show(this, getString(R.string.loading_api_book_title), getString(R.string.loading_api_book_text));
    }

    /**
     * Hides loading spinner
     */
    public void hideBookLoading()
    {
        if (shownProgressDialog != null)
        {
            shownProgressDialog.dismiss();
            shownProgressDialog = null;
        }
    }

    /**
     * Triggers search for book by its ISBN
     * @param isbn      ISBN to be looked for
     */
    public void searchByIsbn(String isbn)
    {
        nameSearchMode = false;
        // create Google books client instance
        GoogleBooksClient client = new GoogleBooksClient();
        // retrieve books
        client.getBooks(isbn, this);

        showBookLoading();
    }

    /**
     * Triggers search for book by its name
     * @param name      Name to be looked for
     */
    public void searchByName(String name)
    {
        // toggle name search mode
        nameSearchMode = true;
        // create Google books client instance
        GoogleBooksClient client = new GoogleBooksClient();
        // retrieve books
        client.getBooks(name, this);

        showBookLoading();
    }

    /**
     * Triggers search using ISBN field
     * @param view      source view
     */
    public void searchByIsbnField(View view)
    {
        EditText isbnEdit = (EditText) findViewById(R.id.isbnEditText);

        // retrieve scanned ISBN
        scannedISBN = isbnEdit.getText().toString();
        // mandatory when searching using it
        if (scannedISBN.length() == 0)
            Toast.makeText(this, getString(R.string.at_first_enter_isbn), Toast.LENGTH_SHORT).show();
        // must have ISBN prefix
        else if (!scannedISBN.substring(0, 3).equals(Constants.BOOK_CODE_BEGIN))
            Toast.makeText(this, getString(R.string.entered_is_not_book), Toast.LENGTH_SHORT).show();
        // if valid, perform search
        else
            searchByIsbn(scannedISBN);
    }

    /**
     * Trigger search using name field
     * @param view      source view
     */
    public void searchByNameField(View view)
    {
        EditText nameEdit = (EditText) findViewById(R.id.nameEditText);

        // retrieve name
        String searchName = nameEdit.getText().toString();
        // name has to be at least 4 characters long
        if (searchName.length() < 4)
            Toast.makeText(this, getString(R.string.at_first_enter_name), Toast.LENGTH_SHORT).show();
        else
            searchByName(searchName);
    }

    /**
     * Sets book info into fields in view
     * @param name          book name
     * @param authors       book authors
     * @param isbn          book ISBN
     */
    public void setBookInfo(String name, String authors, String isbn)
    {
        EditText nameEdit = (EditText) findViewById(R.id.nameEditText);
        nameEdit.setText(name);

        EditText authorEdit = (EditText) findViewById(R.id.authorEditText);
        authorEdit.setText(authors);

        EditText isbnEdit = (EditText) findViewById(R.id.isbnEditText);
        isbnEdit.setText(isbn);
    }

    /**
     * Callback for Google client - is called when book list is successfully retrieved
     * @param bookList
     */
    public void bookListReceived(final List<GoogleBookItem> bookList)
    {
        hideBookLoading();

        // no book found
        if (bookList.size() == 0)
            Toast.makeText(this, getString(R.string.no_book_found), Toast.LENGTH_SHORT).show();
        // just one book found
        else if (bookList.size() == 1)
        {
            GoogleBookItem gbi = bookList.get(0);
            setBookInfo(gbi.name, gbi.authors, gbi.isbn);
        }
        else
        {
            // try to match ISBN if we searched by ISBN
            if (!nameSearchMode)
            {
                // if we found perfect ISBN match, use this book and return
                for (GoogleBookItem gbi : bookList) {
                    if (gbi.isbn.equals(scannedISBN)) {
                        setBookInfo(gbi.name, gbi.authors, gbi.isbn);
                        return;
                    }
                }
            }

            // otherwise offer selection of books found

            final CharSequence[] choices = new CharSequence[bookList.size()];
            int i = 0;
            for (GoogleBookItem gbi : bookList)
                choices[i++] = gbi.toString();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.choose_book_list)
                    .setItems(choices, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which >= 0)
                            {
                                GoogleBookItem gbi = bookList.get(which);
                                setBookInfo(gbi.name, gbi.authors, gbi.isbn);
                            }
                        }
                    });
            builder.create().show();
        }
    }

    /**
     * Activity result callback
     * @param requestCode       code which we requested
     * @param resultCode        result code
     * @param intent            intent with result
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        // we retrieve only scanning result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        // if valid..
        if (scanningResult != null)
        {
            // retrieve scanned code
            scannedISBN = scanningResult.getContents();

            if (scannedISBN != null)
            {
                // and if it's valid ISBN, search using it; otherwise report error
                if (!scannedISBN.substring(0, 3).equals(Constants.BOOK_CODE_BEGIN))
                    Toast.makeText(this, getString(R.string.barscan_is_not_book), Toast.LENGTH_SHORT).show();
                else
                    searchByIsbn(scannedISBN);
            }
        }
        else
            Toast.makeText(this, getString(R.string.barscan_nothing_scanned), Toast.LENGTH_SHORT).show();
    }
}
