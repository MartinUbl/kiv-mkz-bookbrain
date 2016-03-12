package cz.paranoid.mobile.bookbrain.activities;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import cz.paranoid.mobile.bookbrain.R;

import java.util.List;

/**
 * Activity for settings
 */
public class SettingsActivity extends AppCompatActivity
{
    /** preference for borrow days */
    public static final String PREF_DEF_BORROW_DAYS = "def_borrow_days";
    /** preference for notification hour within workdays */
    public static final String PREF_DEF_NOTIFY_HOUR = "def_notify_hour";
    /** preference for notification hour within weekend */
    public static final String PREF_DEF_NOTIFY_HOUR_WEEKEND = "def_notify_hour_weekend";
    /** preference for notification flag */
    public static final String PREF_RETURN_NOTIFY = "return_notify";

    /** default value of borrow days */
    public static final int DEF_VALUE_BORROW_DAYS = 28;
    /** default value for notification hour within workdays */
    public static final int DEF_VALUE_NOTIFY_HOUR = 8;
    /** default value for notification hour within weekends */
    public static final int DEF_VALUE_NOTIFY_HOUR_WEEKEND = 10;

    /**
     * Adapter for TextWatcher interface, since we need only afterTextChanged callback
     */
    private class TextWatcherAdapter implements TextWatcher
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getString(R.string.title_activity_settings));

        // get shared preference instance
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // borrow days edit
        final EditText et = (EditText) findViewById(R.id.borrowTimeEdit);
        et.setText(""+preferences.getInt(PREF_DEF_BORROW_DAYS, DEF_VALUE_BORROW_DAYS));

        // when text changes, immediatelly save
        et.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                try
                {
                    int dval = Integer.parseInt(s.toString());
                    if (dval <= 0)
                        throw new NumberFormatException();

                    preferences.edit().putInt(PREF_DEF_BORROW_DAYS, dval).apply();
                }
                catch (NumberFormatException ex)
                {
                    et.setError(getString(R.string.setting_invalid_num));
                }
            }
        });

        final EditText notifyhedit = (EditText) findViewById(R.id.notifyHourEdit);
        notifyhedit.setText(""+preferences.getInt(PREF_DEF_NOTIFY_HOUR, DEF_VALUE_NOTIFY_HOUR));

        notifyhedit.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                try
                {
                    int dval = Integer.parseInt(s.toString());
                    if (dval <= 0 || dval >= 24)
                        throw new NumberFormatException();

                    preferences.edit().putInt(PREF_DEF_NOTIFY_HOUR, dval).apply();
                }
                catch (NumberFormatException ex)
                {
                    notifyhedit.setError(getString(R.string.setting_invalid_hour));
                }
            }
        });

        final EditText notifyheditwknd = (EditText) findViewById(R.id.notifyHourWeekendEdit);
        notifyheditwknd.setText(""+preferences.getInt(PREF_DEF_NOTIFY_HOUR_WEEKEND, DEF_VALUE_NOTIFY_HOUR_WEEKEND));

        notifyheditwknd.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                try
                {
                    int dval = Integer.parseInt(s.toString());
                    if (dval <= 0 || dval >= 24)
                        throw new NumberFormatException();

                    preferences.edit().putInt(PREF_DEF_NOTIFY_HOUR_WEEKEND, dval).apply();
                }
                catch (NumberFormatException ex)
                {
                    notifyheditwknd.setError(getString(R.string.setting_invalid_hour));
                }
            }
        });

        CheckBox cb = (CheckBox) findViewById(R.id.checkBoxNotify);
        cb.setChecked(preferences.getBoolean(PREF_RETURN_NOTIFY, true));

        notifyhedit.setEnabled(cb.isChecked());
        notifyheditwknd.setEnabled(cb.isChecked());

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.edit().putBoolean(PREF_RETURN_NOTIFY, isChecked).apply();

                notifyhedit.setEnabled(isChecked);
                notifyheditwknd.setEnabled(isChecked);
            }
        });
    }

}
