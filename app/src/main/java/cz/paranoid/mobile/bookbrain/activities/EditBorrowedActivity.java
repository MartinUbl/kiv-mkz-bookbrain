package cz.paranoid.mobile.bookbrain.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import cz.paranoid.mobile.bookbrain.R;
import cz.paranoid.mobile.bookbrain.containers.BorrowedItem;
import cz.paranoid.mobile.bookbrain.containers.StoredItem;
import cz.paranoid.mobile.bookbrain.misc.BookDatabaseHelper;
import cz.paranoid.mobile.bookbrain.misc.DateWorker;

/**
 * Activity for editing borrowed book item
 */
public class EditBorrowedActivity extends AppCompatActivity
{
    /** record we are editing */
    private BorrowedItem editItem;
    /** date selected as return date */
    private Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_borrowed);

        // edited item is taken from intent
        editItem = (BorrowedItem) getIntent().getSerializableExtra("item");

        // view title is determined using book name
        setTitle(editItem.name);

        EditText field;

        // set name field value
        field = (EditText) findViewById(R.id.nameEditText);
        field.setText(editItem.name);

        // set author field value
        field = (EditText) findViewById(R.id.authorEditText);
        field.setText(editItem.author);

        // set note field value
        field = (EditText) findViewById(R.id.noteEditText);
        field.setText(editItem.note);

        // selected date is parsed from stored value
        selectedDate = new Date();
        selectedDate.setTime(editItem.returnDate);

        // retrieve date fields for edit text to be filled
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(editItem.returnDate);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        final TextView dateText = (TextView) findViewById(R.id.returnEditText);
        dateText.setText(day+". "+(month+1)+". "+year);
    }

    /**
     * When button for date choosing was clicked
     * @param v     source view
     */
    public void chooseDateClicked(View v)
    {
        final TextView dateText = (TextView) findViewById(R.id.returnEditText);

        // get selected date, and extract current date fields
        Calendar c = Calendar.getInstance();
        c.setTime(selectedDate);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        final Context that = this;

        // date picker create
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int yearSel, int monthOfYear, int dayOfMonth)
                    {
                        // get calendar instance and set date
                        Calendar c = Calendar.getInstance();
                        c.set(yearSel, monthOfYear, dayOfMonth);
                        // move to next working day
                        c = DateWorker.moveToNextWorkingDay(c);
                        int year = c.get(Calendar.YEAR);
                        int month = c.get(Calendar.MONTH);
                        int day = c.get(Calendar.DAY_OF_MONTH);
                        // if something moved, let user know
                        if (dayOfMonth != c.get(Calendar.DAY_OF_MONTH))
                            Toast.makeText(that, that.getString(R.string.moved_to_next_workday), Toast.LENGTH_LONG).show();

                        // set text to view
                        dateText.setText(day+". "+(month+1)+". "+year);

                        // and update stored date
                        selectedDate = c.getTime();
                    }
                }, year, month, day);
        dpd.show();
    }

    /**
     * Save button was clicked
     * @param view      source view
     */
    public void saveBorrowedButtonClicked(View view)
    {
        // retrieve name field
        EditText nameEdit = (EditText) findViewById(R.id.nameEditText);
        // name is mandatory
        if (nameEdit.getText().length() == 0)
        {
            nameEdit.setError(getString(R.string.form_enter_name));
            return;
        }

        // retrieve the rest of fields
        EditText authorEdit = (EditText) findViewById(R.id.authorEditText);
        EditText noteEdit = (EditText) findViewById(R.id.noteEditText);

        // edit book used database helper
        BookDatabaseHelper bdh = new BookDatabaseHelper(this);
        bdh.editBorrowedBook(editItem.id, nameEdit.getText().toString(), authorEdit.getText().toString(), noteEdit.getText().toString(), selectedDate.getTime());

        // send resulting intent
        Intent resultIntent = new Intent();
        // pass book name to be reported
        resultIntent.putExtra("bookname", nameEdit.getText().toString());
        setResult(RESULT_OK, resultIntent);

        finish();
    }
}
