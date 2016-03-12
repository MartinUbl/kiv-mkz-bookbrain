package cz.paranoid.mobile.bookbrain.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import cz.paranoid.mobile.bookbrain.misc.BookDatabaseHelper;
import cz.paranoid.mobile.bookbrain.R;
import cz.paranoid.mobile.bookbrain.containers.StoredItem;

/**
 * Activity for editing existing stored book item
 */
public class EditStoredActivity extends AppCompatActivity
{
    /** stored book item we are editing */
    private StoredItem editItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_stored);

        // item is taken from intent
        editItem = (StoredItem) getIntent().getSerializableExtra("item");

        // title is determined using stored book name
        setTitle(editItem.name);

        EditText field;

        // retrieve name
        field = (EditText) findViewById(R.id.nameEditText);
        field.setText(editItem.name);

        // retrieve author
        field = (EditText) findViewById(R.id.authorEditText);
        field.setText(editItem.author);

        // retrieve note
        field = (EditText) findViewById(R.id.noteEditText);
        field.setText(editItem.note);
    }

    /**
     * Save button was clicked
     * @param v     source view
     */
    public void saveEditStoredBook(View v)
    {
        // retrieve name
        EditText nameEdit = (EditText) findViewById(R.id.nameEditText);
        // name is mandatory
        if (nameEdit.getText().length() == 0)
        {
            nameEdit.setError(getString(R.string.form_enter_name));
            return;
        }

        // retieve other fields
        EditText authorEdit = (EditText) findViewById(R.id.authorEditText);
        EditText noteEdit = (EditText) findViewById(R.id.noteEditText);

        // edit stored book using database helper
        BookDatabaseHelper bdh = new BookDatabaseHelper(this);
        bdh.editStoredBook(editItem.id, nameEdit.getText().toString(), authorEdit.getText().toString(), noteEdit.getText().toString());

        // send resulting intent
        Intent resultIntent = new Intent();
        // pass book name to be reported
        resultIntent.putExtra("bookname", editItem.name);
        setResult(Activity.RESULT_OK, resultIntent);

        finish();
    }
}
