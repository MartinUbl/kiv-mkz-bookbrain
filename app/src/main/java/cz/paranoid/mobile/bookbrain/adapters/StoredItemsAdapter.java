package cz.paranoid.mobile.bookbrain.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cz.paranoid.mobile.bookbrain.R;
import cz.paranoid.mobile.bookbrain.containers.StoredItem;

/**
 * Adapter for stored book list item
 */
public class StoredItemsAdapter extends ArrayAdapter<StoredItem>
{
    /**
     * Overridden constructor
     * @param context   instance context
     * @param items     ArrayList of stored items
     */
    public StoredItemsAdapter(Context context, ArrayList<StoredItem> items)
    {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        StoredItem bi = getItem(position);

        // if not recycling view... create new
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.stored_item, parent, false);
        // allow long click - due to context menu
        convertView.setLongClickable(true);

        // retrieve and fill fields
        TextView tvName = (TextView) convertView.findViewById(R.id.biBookName);
        TextView tvHome = (TextView) convertView.findViewById(R.id.biBookAuthor);
        tvName.setText(bi.name);
        tvHome.setText(bi.author);

        return convertView;
    }
}