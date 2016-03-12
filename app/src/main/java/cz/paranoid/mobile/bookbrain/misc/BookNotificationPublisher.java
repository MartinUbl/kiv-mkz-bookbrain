package cz.paranoid.mobile.bookbrain.misc;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Calendar;

import cz.paranoid.mobile.bookbrain.R;
import cz.paranoid.mobile.bookbrain.activities.SettingsActivity;
import cz.paranoid.mobile.bookbrain.containers.BorrowedItem;

/**
 * Book notification publisher - called from alarm event
 */
public class BookNotificationPublisher extends BroadcastReceiver
{
    /** How many days before return date to warn (will be rewritten to preference) */
    public static final long ALARM_GROUP_RETURN_TIME = AlarmManager.INTERVAL_DAY * 3;
    /** secure time interval for notify */
    public static final long ALARM_GROUP_RETURN_TIME_INTERVAL = 60000; // 1 minute in milliseconds

    /**
     * Received event from alarm
     * @param context   source context
     * @param intent    intent we build in notification manager
     */
    public void onReceive(Context context, Intent intent)
    {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        // we chose not to notify
        if (!preferences.getBoolean(SettingsActivity.PREF_RETURN_NOTIFY, true))
            return;

        // verify we are intended to notify now according to preferences
        Calendar c = Calendar.getInstance();
        // weekend days
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
        {
            if (c.get(Calendar.HOUR_OF_DAY) < preferences.getInt(SettingsActivity.PREF_DEF_NOTIFY_HOUR_WEEKEND, SettingsActivity.DEF_VALUE_NOTIFY_HOUR_WEEKEND))
                return;
        }
        else // workdays
        {
            if (c.get(Calendar.HOUR_OF_DAY) < preferences.getInt(SettingsActivity.PREF_DEF_NOTIFY_HOUR, SettingsActivity.DEF_VALUE_NOTIFY_HOUR))
                return;
        }

        BookDatabaseHelper bdh = new BookDatabaseHelper(context);
        ArrayList<BorrowedItem> items = bdh.getBorrowedBooks();

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        // go through all borrowed items
        for (BorrowedItem it : items)
        {
            // if we are not supposed to notify due to far return date, ignore this book
            if (it.returnDate > System.currentTimeMillis() + ALARM_GROUP_RETURN_TIME)
                continue;

            // check safe interval
            if (it.lastNotifyTime < (System.currentTimeMillis() - ALARM_GROUP_RETURN_TIME_INTERVAL)/1000)
            {
                c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                c.setTimeInMillis(it.lastNotifyTime*1000);
                // do not notify twice within one day
                if (day == c.get(Calendar.DAY_OF_MONTH))
                    continue;

                Notification nn = BookNotificationManager.getNotification(context, context.getString(R.string.book_return_notify_title), String.format(context.getString(R.string.book_return_notify_body), it.name));
                notificationManager.notify(it.id, nn);
                bdh.setBorrowedBookNotifiedNow(it.id);
            }
        }
    }
}
