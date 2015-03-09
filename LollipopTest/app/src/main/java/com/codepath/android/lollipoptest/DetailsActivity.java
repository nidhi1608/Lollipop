package com.codepath.android.lollipoptest;

import android.animation.Animator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.TextView;

/**
 * Created by nidhi on 2/22/15.
 */
public class DetailsActivity extends ActionBarActivity {
    public static final String EXTRA_CONTACT = "EXTRA_CONTACT";
    private Contact mContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContact = (Contact)getIntent().getExtras().getSerializable(EXTRA_CONTACT);
        setContentView(R.layout.activity_detail);
        final CardAdapter.VH vh = new CardAdapter.VH(findViewById(R.id.cvRoot), this);
        CardAdapter.populate(this, vh, mContact);
        mContact.fetchContactEmails(this);
        mContact.fetchContactNumbers(this);
        final View fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + mContact.numbers.get(0).number.trim() ;
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        fab.setVisibility(View.INVISIBLE);
        fab.postDelayed(new Runnable() {
            @Override
            public void run() {
                // get the center for the clipping circle
                int cx = fab.getWidth() / 2;
                int cy = fab.getHeight() / 2;

                // get the final radius for the clipping circle
                int finalRadius = Math.max(fab.getWidth(), fab.getHeight()) / 2;
                Animator anim = ViewAnimationUtils.createCircularReveal(fab, cx, cy, 0, finalRadius);
                fab.setVisibility(View.VISIBLE);
                anim.start();
            }
        }, 600);
        final TextView tvPhone = (TextView)findViewById(R.id.tvPhone);
        if (mContact.numbers.size() > 0) {
            tvPhone.setText(mContact.numbers.get(0).number);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
