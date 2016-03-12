package cz.paranoid.mobile.bookbrain.misc;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import cz.paranoid.mobile.bookbrain.R;

public class BookNotificationManager
{
    /** notification service identifier */
    public static int SERVICE_NOTIFY_IDENT = 10024;
    /** service ID field name */
    public static String SERVICE_ID = "service-id";
    /** notification check interval */
    public static long SERVICE_NOTIFY_CHECK_INTERVAL = 600000; // in milliseconds (10 minutes)

    /**
     * (Re)starts notification service
     * @param appContext    source context
     */
    public static void startNotifyService(Context appContext)
    {
        Intent serviceIntent = new Intent(appContext, BookNotificationPublisher.class);
        serviceIntent.putExtra(SERVICE_ID, SERVICE_NOTIFY_IDENT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(appContext, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)appContext.getSystemService(Context.ALARM_SERVICE);

        // cancel any previous service instance
        alarmManager.cancel(pendingIntent);
        // schedule new
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SERVICE_NOTIFY_CHECK_INTERVAL, SERVICE_NOTIFY_CHECK_INTERVAL, pendingIntent);
    }

    /**
     * Builds notification
     * @param source    source context
     * @param title     notification title
     * @param content   notification content
     * @return          built notification instance
     */
    public static Notification getNotification(Context source, String title, String content)
    {
        Notification.Builder builder = new Notification.Builder(source);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.mipmap.bookbrain);
        return (builder.build());
    }
}
