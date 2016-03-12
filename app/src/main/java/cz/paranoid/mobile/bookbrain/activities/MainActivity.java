package cz.paranoid.mobile.bookbrain.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import cz.paranoid.mobile.bookbrain.R;
import cz.paranoid.mobile.bookbrain.activities.fragments.TabBorrowedFragment;
import cz.paranoid.mobile.bookbrain.activities.fragments.TabStoredFragment;
import cz.paranoid.mobile.bookbrain.containers.BorrowedItem;
import cz.paranoid.mobile.bookbrain.containers.StoredItem;
import cz.paranoid.mobile.bookbrain.misc.BookNotificationManager;

/**
 * Main application activity - container for tab view
 */
public class MainActivity extends AppCompatActivity
{
    /** adapter for tabs */
    private SectionsPagerAdapter sectionsPagerAdapter;
    /** view pager component for tabs */
    private ViewPager viewPager;
    /** stored framents for each tab */
    private Map<Integer, Fragment> fragmentMap = new HashMap<Integer, Fragment>();

    // activity request codes

    /** code for add stored */
    private static final int REQ_CODE_ADD_STORED = 1;
    /** code for edit stored */
    private static final int REQ_CODE_EDIT_STORED = 2;
    /** code for add borrowed */
    private static final int REQ_CODE_ADD_BORROWED = 3;
    /** code for edit borrowed */
    private static final int REQ_CODE_EDIT_BORROWED = 4;
    /** code for move stored to borrowed */
    private static final int REQ_CODE_MOVE_STORED = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of sections of the activity.
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // (re)start background notification service
        BookNotificationManager.startNotifyService(getApplicationContext());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here

        int id = item.getItemId();

        Intent in = null;

        switch (id)
        {
            // settings link
            case R.id.action_settings:
                in = new Intent(this, SettingsActivity.class);
                startActivity(in);
                return true;
            // about link
            case R.id.action_about:
                in = new Intent(this, AboutActivity.class);
                startActivity(in);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Add stored book button pressed
     * @param v     source view
     */
    public void addStoredButtonPressed(View v)
    {
        Intent in = new Intent(this, AddStoredActivity.class);
        startActivityForResult(in, REQ_CODE_ADD_STORED);
    }

    /**
     * Edit stored book menu button pressed
     * @param item      Stored item to be edited
     */
    public void editStoredMenuPressed(StoredItem item)
    {
        Intent in = new Intent(this, EditStoredActivity.class);
        in.putExtra("item", item);
        startActivityForResult(in, REQ_CODE_EDIT_STORED);
    }

    /**
     * Add borrowed book button pressed
     * @param v     source view
     */
    public void addBorrowedButtonPressed(View v)
    {
        Intent in = new Intent(this, AddBorrowedActivity.class);
        startActivityForResult(in, REQ_CODE_ADD_BORROWED);
    }

    /**
     * Edit borrowed book menu button pressed
     * @param item      Borrowed item to be edited
     */
    public void editBorrowedMenuPressed(BorrowedItem item)
    {
        Intent in = new Intent(this, EditBorrowedActivity.class);
        in.putExtra("item", item);
        startActivityForResult(in, REQ_CODE_EDIT_BORROWED);
    }

    /**
     * Move stored book to borrowed menu button pressed
     * @param item      Stored item to be moved
     */
    public void moveStoredToBorrowedMenuPressed(StoredItem item)
    {
        Intent in = new Intent(this, AddBorrowedActivity.class);
        in.putExtra("storedItem", item);
        startActivityForResult(in, REQ_CODE_MOVE_STORED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // if everything's ok
        if (resultCode == Activity.RESULT_OK)
        {
            // refresh stored items flag
            boolean refreshStored = false;
            // refresh borrowed items flag
            boolean refreshBorrowed = false;

            switch (requestCode)
            {
                case REQ_CODE_ADD_STORED:
                    refreshStored = true;
                    Toast.makeText(this, String.format(getString(R.string.stored_book_added), data.getStringExtra("bookname")), Toast.LENGTH_SHORT).show();
                    break;
                case REQ_CODE_EDIT_STORED:
                    refreshStored = true;
                    Toast.makeText(this, String.format(getString(R.string.stored_book_edited), data.getStringExtra("bookname")), Toast.LENGTH_SHORT).show();
                    break;
                case REQ_CODE_ADD_BORROWED:
                    refreshBorrowed = true;
                    Toast.makeText(this, String.format(getString(R.string.stored_book_added), data.getStringExtra("bookname")), Toast.LENGTH_SHORT).show();
                    break;
                case REQ_CODE_EDIT_BORROWED:
                    refreshBorrowed = true;
                    Toast.makeText(this, String.format(getString(R.string.stored_book_edited), data.getStringExtra("bookname")), Toast.LENGTH_SHORT).show();
                    break;
                case REQ_CODE_MOVE_STORED:
                    refreshStored = true;
                    refreshBorrowed = true;
                    Toast.makeText(this, String.format(getString(R.string.stored_book_borrowed), data.getStringExtra("bookname")), Toast.LENGTH_SHORT).show();
                    break;
            }

            if (refreshStored && fragmentMap.get(0) != null)
                ((TabStoredFragment) fragmentMap.get(0)).refreshStoredBooksList(findViewById(R.id.container));
            if (refreshBorrowed && fragmentMap.get(1) != null)
                ((TabBorrowedFragment) fragmentMap.get(1)).refreshBorrowedBooksList(findViewById(R.id.container));
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {
        private Activity myContext;

        public SectionsPagerAdapter(FragmentManager fm, Activity where)
        {
            super(fm);
            myContext = where;
        }

        @Override
        public Fragment getItem(int position)
        {
            switch (position)
            {
                // stored items tab
                case 0:
                {
                    TabStoredFragment frag = TabStoredFragment.newInstance();
                    frag.scheduleRefreshStoredBooksList();
                    fragmentMap.put(0, frag);
                    return frag;
                }
                // borrowed items tab
                case 1:
                {
                    TabBorrowedFragment frag = TabBorrowedFragment.newInstance();
                    frag.scheduleRefreshBorrowedBooksList();
                    fragmentMap.put(1, frag);
                    return frag;
                }
            }
            return null;
        }

        @Override
        public int getCount()
        {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position)
            {
                case 0:
                    return getString(R.string.stored_books_title);
                case 1:
                    return getString(R.string.borrowed_books_title);
            }
            return null;
        }
    }
}
