package cz.paranoid.mobile.bookbrain.activities.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cz.paranoid.mobile.bookbrain.activities.MainActivity;
import cz.paranoid.mobile.bookbrain.misc.BookDatabaseHelper;
import cz.paranoid.mobile.bookbrain.R;
import cz.paranoid.mobile.bookbrain.adapters.StoredItemsAdapter;
import cz.paranoid.mobile.bookbrain.containers.StoredItem;

/**
 * Activity fragment for stored books list
 */
public class TabStoredFragment extends Fragment
{
    /** Menu action for item delete */
    private static final int ACTION_DELETE = 1;
    /** Menu action for item edit */
    private static final int ACTION_EDIT = 2;
    /** Menu action for item borrow */
    private static final int ACTION_BORROW = 120;

    /** Adapter for list items */
    private ArrayAdapter listAdapter;

    /**
     * Builds new instance of this fragment
     * @return stored fragment instance
     */
    public static TabStoredFragment newInstance()
    {
        TabStoredFragment fragment = new TabStoredFragment();
        return fragment;
    }

    /**
     * Empty constructor
     */
    public TabStoredFragment()
    {
    }

    /**
     * Schedule stored books list refresh
     */
    public void scheduleRefreshStoredBooksList()
    {
        final TabStoredFragment that = this;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                that.refreshStoredBooksList(that.getView());
            }
        }, 500);
    }

    /**
     * Immediately refreshes stored books list
     * @param lv    listview to refresh
     * @param tv    textview with "empty" message
     */
    public void refreshStoredBooksList(ListView lv, TextView tv)
    {
        ArrayList<StoredItem> items = (new BookDatabaseHelper(getContext())).getStoredBooks();

        listAdapter = new StoredItemsAdapter(getContext(), items);
        lv.setAdapter(listAdapter);

        // if the list is empty, show "list is empty" text
        if (items.size() == 0)
            tv.setVisibility(View.VISIBLE);
        else // otherwise hide
            tv.setVisibility(View.INVISIBLE);
    }

    /**
     * Immediately refreshes borrowed books list
     * @param v     source view
     */
    public void refreshStoredBooksList(View v)
    {
        ListView listview = (ListView) v.findViewById(R.id.listview);
        TextView tv = (TextView) v.findViewById(R.id.listviewEmptyText);
        refreshStoredBooksList(listview, tv);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // inflate fragment view
        View rootView = inflater.inflate(R.layout.fragment_main_stored, container, false);

        // refresh list
        refreshStoredBooksList(rootView);

        // register listview items for longclick
        ListView listview = (ListView) rootView.findViewById(R.id.listview);
        registerForContextMenu(listview);

        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        // create contextmenu for listview item
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        // use item name as context menu title
        String title = ((StoredItem) listAdapter.getItem(info.position)).name;
        menu.setHeaderTitle(title);

        // add actions
        menu.add(Menu.NONE, ACTION_EDIT, Menu.NONE, getString(R.string.edit_stored_book));
        menu.add(Menu.NONE, ACTION_BORROW, Menu.NONE, getString(R.string.move_stored_to_borrowed_book));
        menu.add(Menu.NONE, ACTION_DELETE, Menu.NONE, getString(R.string.delete_stored_book));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        // if the position is further than our range, return
        if (listAdapter.getCount() <= info.position)
            return super.onContextItemSelected(item);

        // retrieve borrowed item instance before handling
        StoredItem sel = ((StoredItem) listAdapter.getItem(info.position));
        switch (item.getItemId())
        {
            // delete item from item list
            case ACTION_DELETE:
            {
                Toast.makeText(getContext(), String.format(getString(R.string.stored_book_deleted), sel.name), Toast.LENGTH_SHORT).show();

                // delete stored book from database using helper
                BookDatabaseHelper bdh = new BookDatabaseHelper(getContext());
                bdh.removeStoredBook(sel.id);
                // refresh stored book list
                refreshStoredBooksList(getView());
                return true;
            }
            // edit item
            case ACTION_EDIT:
            {
                ((MainActivity)getActivity()).editStoredMenuPressed(sel);
                return true;
            }
            // move to borrowed books
            case ACTION_BORROW:
            {
                ((MainActivity)getActivity()).moveStoredToBorrowedMenuPressed(sel);
                return true;
            }
            // any other action
            default:
                return super.onContextItemSelected(item);
        }
    }
}
