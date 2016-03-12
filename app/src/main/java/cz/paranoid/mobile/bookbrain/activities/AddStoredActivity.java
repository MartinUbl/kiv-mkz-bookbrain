package cz.paranoid.mobile.bookbrain.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import cz.paranoid.mobile.bookbrain.misc.BookDatabaseHelper;
import cz.paranoid.mobile.bookbrain.R;

/**
 * Activity for adding new stored book
 */
public class AddStoredActivity extends AddBookAbstractActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stored);
        // at first, mark as "failed"
        setResult(RESULT_CANCELED);
    }

    public void addButtonClicked(View view)
    {
        // retrieve name field
        EditText nameEdit = (EditText) findViewById(R.id.nameEditText);
        // name is mandatory
        if (nameEdit.getText().length() == 0)
        {
            nameEdit.setError(getString(R.string.form_enter_name));
            return;
        }

        // retrieve other fields
        EditText authorEdit = (EditText) findViewById(R.id.authorEditText);
        EditText isbnEdit = (EditText) findViewById(R.id.isbnEditText);
        EditText noteEdit = (EditText) findViewById(R.id.noteEditText);

        // adds new stored book via database helper
        BookDatabaseHelper bdh = new BookDatabaseHelper(this);
        bdh.addStoredBook(nameEdit.getText().toString(), authorEdit.getText().toString(), isbnEdit.getText().toString(), noteEdit.getText().toString());

        // send result via intent
        Intent resultIntent = new Intent();
        // store book name to be reported
        resultIntent.putExtra("bookname", nameEdit.getText().toString());
        setResult(RESULT_OK, resultIntent);

        finish();
    }
}
