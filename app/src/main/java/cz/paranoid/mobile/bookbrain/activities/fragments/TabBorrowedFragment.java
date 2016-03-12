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
import cz.paranoid.mobile.bookbrain.containers.StoredItem;
import cz.paranoid.mobile.bookbrain.misc.BookDatabaseHelper;
import cz.paranoid.mobile.bookbrain.R;
import cz.paranoid.mobile.bookbrain.adapters.BorrowedItemsAdapter;
import cz.paranoid.mobile.bookbrain.containers.BorrowedItem;

/**
 * Activity fragment for borrowed books list
 */
public class TabBorrowedFragment extends Fragment
{
    /** Menu action for item delete */
    private static final int ACTION_DELETE = 3;
    /** Menu action for item edit */
    private static final int ACTION_EDIT = 4;

    /** Adapter for list items */
    private ArrayAdapter listAdapter;

    /**
     * Builds new instance of this fragment
     * @return borrowed fragment instance
     */
    public static TabBorrowedFragment newInstance()
    {
        TabBorrowedFragment fragment = new TabBorrowedFragment();
        return fragment;
    }

    /**
     * Empty constructor
     */
    public TabBorrowedFragment()
    {
    }

    /**
     * Schedule borrowed books list refresh
     */
    public void scheduleRefreshBorrowedBooksList()
    {
        final TabBorrowedFragment that = this;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {that.refreshBorrowedBooksList(that.getView());
            }
        }, 500);
    }

    /**
     * Immediately refreshes borrowed books list
     * @param lv    listview to refresh
     * @param tv    textview with "empty" message
     */
    private void refreshBorrowedBooksList(ListView lv, TextView tv)
    {
        ArrayList<BorrowedItem> items = (new BookDatabaseHelper(getContext())).getBorrowedBooks();

        listAdapter = new BorrowedItemsAdapter(getContext(), items);
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
    public void refreshBorrowedBooksList(View v)
    {
        ListView listview = (ListView) v.findViewById(R.id.listview2);
        TextView tv = (TextView) v.findViewById(R.id.listview2EmptyText);
        refreshBorrowedBooksList(listview, tv);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // inflate fragment view
        View rootView = inflater.inflate(R.layout.fragment_main_borrowed, container, false);

        // refresh list
        refreshBorrowedBooksList(rootView);

        // register listview items for longclick
        ListView listview = (ListView) rootView.findViewById(R.id.listview2);
        registerForContextMenu(listview);

        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        // create contextmenu for listview item
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        // use item name as context menu title
        String title = ((BorrowedItem) listAdapter.getItem(info.position)).name;
        menu.setHeaderTitle(title);

        // add actions
        menu.add(Menu.NONE, ACTION_EDIT, Menu.NONE, getString(R.string.edit_stored_book));
        menu.add(Menu.NONE, ACTION_DELETE, Menu.NONE, getString(R.string.delete_stored_book));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        // if the position is further than our range, return
        if (listAdapter.getCount() <= info.position)
            return super.onContextItemSelected(item);

        // retrieve borrowed item instance before handling
        BorrowedItem sel = ((BorrowedItem) listAdapter.getItem(info.position));
        switch (item.getItemId())
        {
            // delete item from list
            case ACTION_DELETE:
            {
                Toast.makeText(getContext(), String.format(getString(R.string.stored_book_deleted), sel.name), Toast.LENGTH_SHORT).show();

                // remove book from database using helper
                BookDatabaseHelper bdh = new BookDatabaseHelper(getContext());
                bdh.removeBorrowedBook(sel.id);
                // don't forget to refresh list of items
                refreshBorrowedBooksList(getView());
                return true;
            }
            // edit item in list
            case ACTION_EDIT:
            {
                // main activity will take care of the rest
                ((MainActivity)getActivity()).editBorrowedMenuPressed(sel);
                return true;
            }
            // any other action
            default:
                return super.onContextItemSelected(item);
        }
    }
}
