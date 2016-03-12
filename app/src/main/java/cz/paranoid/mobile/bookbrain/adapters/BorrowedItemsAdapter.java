package cz.paranoid.mobile.bookbrain.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.paranoid.mobile.bookbrain.R;
import cz.paranoid.mobile.bookbrain.containers.BorrowedItem;
import cz.paranoid.mobile.bookbrain.misc.BookNotificationPublisher;

/**
 * Adapter for borrowed book list item
 */
public class BorrowedItemsAdapter extends ArrayAdapter<BorrowedItem>
{
    /**
     * Overridden constructor
     * @param context   instance context
     * @param items     ArrayList of items
     */
    public BorrowedItemsAdapter(Context context, ArrayList<BorrowedItem> items)
    {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        BorrowedItem bi = getItem(position);

        // when not recycling view... create new
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.borrowed_item, parent, false);
        // allow long click - due to context menu
        convertView.setLongClickable(true);

        // instantiate return date
        Date returnDate = new Date(bi.returnDate);

        // retrieve fields from view
        TextView tvName = (TextView) convertView.findViewById(R.id.biBookName);
        TextView tvHome = (TextView) convertView.findViewById(R.id.biBookAuthor);
        TextView tvReturnDate = (TextView) convertView.findViewById(R.id.biReturnDue);
        // fill fields with data
        tvName.setText(bi.name);
        tvHome.setText(bi.author);
        tvReturnDate.setText(String.format(convertView.getContext().getString(R.string.return_due), new SimpleDateFormat("d. M. yyyy").format(returnDate)));

        // if the return date is in next X days, set text color to red
        if (returnDate.getTime() < System.currentTimeMillis() + BookNotificationPublisher.ALARM_GROUP_RETURN_TIME)
            tvReturnDate.setTextColor(convertView.getResources().getColor(R.color.colorPruser));

        return convertView;
    }
}
