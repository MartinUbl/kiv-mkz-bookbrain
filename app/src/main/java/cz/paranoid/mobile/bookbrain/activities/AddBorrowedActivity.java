package cz.paranoid.mobile.bookbrain.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import cz.paranoid.mobile.bookbrain.containers.StoredItem;
import cz.paranoid.mobile.bookbrain.misc.BookDatabaseHelper;
import cz.paranoid.mobile.bookbrain.R;
import cz.paranoid.mobile.bookbrain.misc.DateWorker;

/**
 * Activity for adding new borrowed book
 */
public class AddBorrowedActivity extends AddBookAbstractActivity
{
    /** Currently selected date */
    protected Date selectedDate = null;
    /** If adding book from stored items tab, this is the reference to that item */
    protected StoredItem sourceItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_borrowed);
        setResult(RESULT_CANCELED);

        // if there was some stored item present in intent param, retain it
        StoredItem item = null;
        try
        {
            item = (StoredItem) getIntent().getSerializableExtra("storedItem");
        }
        catch (Exception e)
        {
            //
        }

        // parse everything needed
        if (item != null)
        {
            // retrieve edittext references
            EditText nameEdit = (EditText) findViewById(R.id.nameEditText);
            EditText authorEdit = (EditText) findViewById(R.id.authorEditText);
            EditText isbnEdit = (EditText) findViewById(R.id.isbnEditText);
            EditText noteEdit = (EditText) findViewById(R.id.noteEditText);

            // fill editext with valid data
            nameEdit.setText(item.name);
            authorEdit.setText(item.author);
            isbnEdit.setText(item.isbn);
            noteEdit.setText(item.note);

            // store source item
            sourceItem = item;
        }
        else
        {
            //
        }

        // get preferences instance
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // create date of book return
        Calendar c = Calendar.getInstance();
        // using preferences, move return date by configured days
        c.add(Calendar.DATE, preferences.getInt(SettingsActivity.PREF_DEF_BORROW_DAYS, 28));
        // then move to next working day, because of opening time in libraries
        c = DateWorker.moveToNextWorkingDay(c);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // store this date
        selectedDate = c.getTime();

        // set this text to TextView
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

                        // and update stored time
                        selectedDate = c.getTime();
                    }
                }, year, month, day);
        dpd.show();
    }

    /**
     * When "add" button was clicked
     * @param view      source view
     */
    public void addButtonClicked(View view)
    {
        // get name field
        EditText nameEdit = (EditText) findViewById(R.id.nameEditText);
        // name is mandatory
        if (nameEdit.getText().length() == 0)
        {
            nameEdit.setError(getString(R.string.form_enter_name));
            return;
        }

        // retrieve the rest of fields
        EditText authorEdit = (EditText) findViewById(R.id.authorEditText);
        EditText isbnEdit = (EditText) findViewById(R.id.isbnEditText);
        EditText noteEdit = (EditText) findViewById(R.id.noteEditText);

        // add borrowed book using database helper
        BookDatabaseHelper bdh = new BookDatabaseHelper(this);
        bdh.addBorrowedBook(nameEdit.getText().toString(), authorEdit.getText().toString(), isbnEdit.getText().toString(), noteEdit.getText().toString(), selectedDate.getTime());

        // if we transferred book from stored list, remove it from there
        if (sourceItem != null)
            bdh.removeStoredBook(sourceItem.id);

        // send result via intent
        Intent resultIntent = new Intent();
        // put book name to be reported
        resultIntent.putExtra("bookname", nameEdit.getText().toString());
        setResult(RESULT_OK, resultIntent);

        finish();
    }
}
