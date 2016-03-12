package cz.paranoid.mobile.bookbrain.misc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import cz.paranoid.mobile.bookbrain.containers.BorrowedItem;
import cz.paranoid.mobile.bookbrain.containers.StoredItem;

/**
 * Application database interface helper
 */
public class BookDatabaseHelper extends SQLiteOpenHelper
{
    /** current database version */
    public static final int DATABASE_VERSION = 3;
    /** database file name */
    public static final String DATABASE_NAME = "BookDatabase.db";

    /** stored books table */
    private static final String TABLE_STORED_BOOKS = "stored_books";
    /** borrowed books table */
    private static final String TABLE_BORROWED_BOOKS = "borrowed_books";

    /** ID column */
    private static final String COL_ID = "id";
    /** book name column */
    private static final String COL_NAME = "name";
    /** authors column */
    private static final String COL_AUTHOR = "author";
    /** ISBN column */
    private static final String COL_ISBN = "isbn";
    /** added date column */
    private static final String COL_ADDED_DATE = "added_date";
    /** return date column */
    private static final String COL_RETURN_DATE = "return_date";
    /** date of last notification column */
    private static final String COL_LAST_NOTIFY = "last_notify_date";
    /** custom note column */
    private static final String COL_NOTE = "note";

    /** SQL query for creating stored books table */
    private static final String SQL_CREATE_STORED_BOOKS =
            "CREATE TABLE " + TABLE_STORED_BOOKS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY," +
                    COL_NAME + " TEXT," +
                    COL_AUTHOR + " TEXT," +
                    COL_ISBN + " TEXT," +
                    COL_ADDED_DATE + " INTEGER," +
                    COL_NOTE + " TEXT" +
            " )";

    /** SQL query for borrowed books table */
    private static final String SQL_CREATE_BORROWED_BOOKS =
            "CREATE TABLE " + TABLE_BORROWED_BOOKS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY," +
                    COL_NAME + " TEXT," +
                    COL_AUTHOR + " TEXT," +
                    COL_ISBN + " TEXT," +
                    COL_ADDED_DATE + " INTEGER," +
                    COL_RETURN_DATE + " INTEGER," +
                    COL_LAST_NOTIFY + " INTEGER," +
                    COL_NOTE + " TEXT" +
                    " )";

    /** SQL query for upgrading from version 2 */
    private static final String SQL_VER_2 = "ALTER TABLE " + TABLE_BORROWED_BOOKS + " ADD COLUMN " + COL_LAST_NOTIFY + " INTEGER DEFAULT 0";

    /**
     * Overridden constructor
     * @param context   instance context
     */
    public BookDatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * When database is created
     * @param db        database object instance
     */
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_STORED_BOOKS);
        db.execSQL(SQL_CREATE_BORROWED_BOOKS);
    }

    /**
     * When database is upgraded (not when creating database!)
     * @param db            database object instance
     * @param oldVersion    old DB version
     * @param newVersion    new DB version
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        switch (oldVersion)
        {
            case 1:
                db.execSQL(SQL_CREATE_BORROWED_BOOKS);
            case 2:
                db.execSQL(SQL_VER_2);
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // we won't downgrade
    }

    /** Model implementation **/

    /**
     * Adds new stored book to database
     * @param name      book name
     * @param author    book author
     * @param isbn      book ISBN
     * @param note      custom book note
     * @return          inserted book ID
     */
    public long addStoredBook(String name, String author, String isbn, String note)
    {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_AUTHOR, author);
        values.put(COL_ISBN, isbn);
        values.put(COL_ADDED_DATE, System.currentTimeMillis()/1000);
        values.put(COL_NOTE, note);

        return db.insert(TABLE_STORED_BOOKS, COL_ID, values);
    }

    /**
     * Adds new borrowed book to database
     * @param name          book name
     * @param author        book author
     * @param isbn          book ISBN
     * @param note          custom book note
     * @param returndate    date of return
     * @return              inserted book ID
     */
    public long addBorrowedBook(String name, String author, String isbn, String note, long returndate)
    {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_AUTHOR, author);
        values.put(COL_ISBN, isbn);
        values.put(COL_ADDED_DATE, System.currentTimeMillis()/1000);
        values.put(COL_RETURN_DATE, returndate);
        values.put(COL_LAST_NOTIFY, 0);
        values.put(COL_NOTE, note);

        return db.insert(TABLE_BORROWED_BOOKS, COL_ID, values);
    }

    /**
     * Updates notified timestamp
     * @param id    borrowed book ID
     */
    public void setBorrowedBookNotifiedNow(int id)
    {
        SQLiteDatabase db = getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_LAST_NOTIFY, System.currentTimeMillis()/1000);

        String selection = COL_ID + " = ?";
        String[] selectionArgs = { ""+id };

        db.update(TABLE_BORROWED_BOOKS, values, selection, selectionArgs);
    }

    /**
     * Edits stored book item
     * @param id        item ID
     * @param name      book name
     * @param author    book author
     * @param note      custom note
     */
    public void editStoredBook(int id, String name, String author, String note)
    {
        SQLiteDatabase db = getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_AUTHOR, author);
        values.put(COL_NOTE, note);

        String selection = COL_ID + " = ?";
        String[] selectionArgs = { ""+id };

        db.update(TABLE_STORED_BOOKS, values, selection, selectionArgs);
    }

    /**
     * Edits borrowed book record
     * @param id            item ID
     * @param name          book name
     * @param author        book author
     * @param note          custom book note
     * @param returndate    date of book return
     */
    public void editBorrowedBook(int id, String name, String author, String note, long returndate)
    {
        SQLiteDatabase db = getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_AUTHOR, author);
        values.put(COL_RETURN_DATE, returndate);
        values.put(COL_NOTE, note);

        String selection = COL_ID + " = ?";
        String[] selectionArgs = { ""+id };

        db.update(TABLE_BORROWED_BOOKS, values, selection, selectionArgs);
    }

    /**
     * Removes stored book from database
     * @param id    stored item ID
     */
    public void removeStoredBook(int id)
    {
        SQLiteDatabase db = getWritableDatabase();

        String selection = COL_ID + " = ?";
        String[] selectionArgs = { ""+id };

        db.delete(TABLE_STORED_BOOKS, selection, selectionArgs);
    }

    /**
     * Removes borrowed book from database
     * @param id    borrowed item ID
     */
    public void removeBorrowedBook(int id)
    {
        SQLiteDatabase db = getWritableDatabase();

        String selection = COL_ID + " = ?";
        String[] selectionArgs = { ""+id };

        db.delete(TABLE_BORROWED_BOOKS, selection, selectionArgs);
    }

    /**
     * Retrieves arrayList of stored books
     * @return stored books
     */
    public ArrayList<StoredItem> getStoredBooks()
    {
        SQLiteDatabase db = getReadableDatabase();

        // which columns we will select
        String[] projection = {COL_ID, COL_NAME, COL_AUTHOR, COL_ISBN, COL_ADDED_DATE, COL_NOTE};
        // sort by added date
        String sortOrder =  COL_ADDED_DATE + " DESC";

        Cursor c = db.query(
                TABLE_STORED_BOOKS, // query table
                projection,         // columns to return
                null,               // WHERE columns
                null,               // WHERE values
                null,               // group
                null,               // group filter
                sortOrder           // order
        );

        ArrayList<StoredItem> toret = new ArrayList<StoredItem>();

        if (c.getCount() == 0)
            return toret;

        c.moveToFirst();
        do
        {
            toret.add(new StoredItem(
                    c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                    c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                    c.getString(c.getColumnIndexOrThrow(COL_AUTHOR)),
                    c.getString(c.getColumnIndexOrThrow(COL_ISBN)),
                    c.getInt(c.getColumnIndexOrThrow(COL_ADDED_DATE)),
                    c.getString(c.getColumnIndexOrThrow(COL_NOTE))
            ));
        }
        while (c.moveToNext());

        c.close();

        return toret;
    }

    /**
     * Retrieves arrayList of borrowed books
     * @return borrowed books
     */
    public ArrayList<BorrowedItem> getBorrowedBooks()
    {
        SQLiteDatabase db = getReadableDatabase();

        // which columns we will select
        String[] projection = {COL_ID, COL_NAME, COL_AUTHOR, COL_ISBN, COL_ADDED_DATE, COL_RETURN_DATE, COL_LAST_NOTIFY, COL_NOTE};
        // sort by return date
        String sortOrder =  COL_RETURN_DATE + " ASC";

        Cursor c = db.query(
                TABLE_BORROWED_BOOKS, // query table
                projection,         // columns to return
                null,               // WHERE columns
                null,               // WHERE values
                null,               // group
                null,               // group filter
                sortOrder           // order
        );

        ArrayList<BorrowedItem> toret = new ArrayList<BorrowedItem>();

        if (c.getCount() == 0)
            return toret;

        c.moveToFirst();
        do
        {
            BorrowedItem bi = new BorrowedItem(
                    c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                    c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                    c.getString(c.getColumnIndexOrThrow(COL_AUTHOR)),
                    c.getString(c.getColumnIndexOrThrow(COL_ISBN)),
                    c.getInt(c.getColumnIndexOrThrow(COL_ADDED_DATE)),
                    c.getLong(c.getColumnIndexOrThrow(COL_RETURN_DATE)),
                    c.getString(c.getColumnIndexOrThrow(COL_NOTE))
            );

            bi.lastNotifyTime = c.getLong(c.getColumnIndexOrThrow(COL_LAST_NOTIFY));

            toret.add(bi);
        }
        while (c.moveToNext());

        c.close();

        return toret;
    }
}
