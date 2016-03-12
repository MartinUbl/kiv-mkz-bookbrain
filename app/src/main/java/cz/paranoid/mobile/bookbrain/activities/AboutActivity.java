package cz.paranoid.mobile.bookbrain.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import cz.paranoid.mobile.bookbrain.R;

/**
 * Simple "about" activity
 */
public class AboutActivity extends AppCompatActivity
{
    private int tapCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // set title
        setTitle(getString(R.string.action_about));
    }

    /**
     * OnTap method for main title - contains easter egg
     * @param v     current view
     */
    public void onTitleTap(View v)
    {
        tapCounter++;

        // at each 4th tap, display toast message
        // this easter egg is there specially for Mel, thank you :)
        if (tapCounter % 4 == 0)
            Toast.makeText(this, ":-*", Toast.LENGTH_SHORT).show();
    }
}
