package com.codepath.android.lollipopexercise.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.android.lollipopexercise.R;
import com.codepath.android.lollipopexercise.adapters.CardAdapter;


public class MainActivity extends ActionBarActivity {
    // Defines the id of the loader for later reference
    public static final int CONTACT_LOADER_ID = 78;
    private CardAdapter mAdapter;
    // Defines the asynchronous callback for the contacts data loader
    private LoaderManager.LoaderCallbacks<Cursor> mContactsLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Find RecyclerView and bind to adapter
        final RecyclerView rvCards = (RecyclerView) findViewById(R.id.rvCards);
        rvCards.setHasFixedSize(true);
        // Define 2 column grid layout
        final GridLayoutManager layout = new GridLayoutManager(MainActivity.this, 2);
        rvCards.setLayoutManager(layout);
        mAdapter = new CardAdapter(MainActivity.this, null);
        // Bind adapter to list
        rvCards.setAdapter(mAdapter);
        mContactsLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
            // Create and return the actual cursor loader for the contacts data
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                // Define the columns to retrieve
                String[] projectionFields = new String[]{ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                        ContactsContract.Contacts.PHOTO_URI,
                        ContactsContract.Contacts.IN_VISIBLE_GROUP};
                String selection = ContactsContract.Contacts.PHOTO_URI + " IS NOT NULL AND " + ContactsContract.Contacts.IN_VISIBLE_GROUP + " = 1";
                // Construct the loader
                CursorLoader cursorLoader = new CursorLoader(MainActivity.this,
                        ContactsContract.Contacts.CONTENT_URI, // URI
                        projectionFields,  // projection fields
                        selection, // the selection criteria
                        null, // the selection args
                        projectionFields[1] // the sort order
                );
                // Return the loader for use
                return cursorLoader;
            }

            // When the system finishes retrieving the Cursor through the CursorLoader,
            // a call to the onLoadFinished() method takes place.
            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                // The swapCursor() method assigns the new Cursor to the adapter
                mAdapter.swapCursor(cursor);
            }

            // This method is triggered when the loader is being reset
            // and the loader data is no longer available. Called if the data
            // in the provider changes and the Cursor becomes stale.
            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                // Clear the Cursor we were using with another call to the swapCursor()
                mAdapter.swapCursor(null);
            }
        };
        restartLoader();
    }

    private void restartLoader() {
        // Initialize the loader with a special ID and the defined callbacks from above
        getSupportLoaderManager().initLoader(CONTACT_LOADER_ID, new Bundle(), mContactsLoader);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
