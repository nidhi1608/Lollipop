package com.codepath.android.lollipopexercise.models;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import com.codepath.android.lollipopexercise.R;
import com.codepath.android.lollipopexercise.app.LollipopExerciseApp;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Contact implements Serializable {
    public String id;
    public String name;
    public String thumbnailUri;
    public ArrayList<ContactPhone> numbers;
    private static HashMap<String, Integer> color = new java.util.HashMap<>();

    public Contact(String id, String thumbnailUri, String name) {
        this.id = id;
        this.name = name;
        this.thumbnailUri = thumbnailUri;
        this.numbers = new ArrayList<>();
    }

    public int getColor() {
        return color.containsKey(id) ? color.get(id) : Color.GRAY;
    }

    public void setColor(int colorValue) {
        color.put(id, colorValue);
    }

    public void addNumber(String number, String type) {
        numbers.add(new ContactPhone(number, type));
    }

    public void fetchContactNumbers(final Context context) {
        if (numbers.size() != 0) return;
        /* Get numbers */

        // Define the columns to retrieve
        final String[] numberProjection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE, };

        // Construct the cursor
        Cursor phone = new CursorLoader(context, ContactsContract.CommonDataKinds.Phone.CONTENT_URI, // URI
                numberProjection, // projection fields
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ?", // the selection criteria
                new String[] { String.valueOf(id) }, // the selection args
                null // the sort order
        ).loadInBackground();

        if (phone.moveToFirst()) {
            final int contactNumberColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            final int contactTypeColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);

            while (!phone.isAfterLast()) {
                final String number = phone.getString(contactNumberColumnIndex);
                final int type = phone.getInt(contactTypeColumnIndex);
                String customLabel = "Custom";
                CharSequence phoneType =
                        ContactsContract.CommonDataKinds.Phone.getTypeLabel(
                                context.getResources(), type, customLabel);
                addNumber(number, phoneType.toString());
                phone.moveToNext();
            }

        }
        phone.close();
    }

    public static void addContacts(final Context context) {
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object[] params) {
                addContact("Aaron", context.getResources().getDrawable(R.drawable.person_one), "4153508880");
                addContact("Abby", context.getResources().getDrawable(R.drawable.person_two), "4153508881");
//        addContact("Abel", context.getResources().getDrawable(R.drawable.person_three), "4153508882");
                addContact("Abram", context.getResources().getDrawable(R.drawable.person_four), "4153508883");
                addContact("Ada", context.getResources().getDrawable(R.drawable.person_five), "4153508884");
                addContact("Adam", context.getResources().getDrawable(R.drawable.person_six), "4153508885");
//        addContact("Adda", context.getResources().getDrawable(R.drawable.person_seven), "4153508886");
//        addContact("Addie", context.getResources().getDrawable(R.drawable.person_eight), "4153508887");
                addContact("Addison", context.getResources().getDrawable(R.drawable.person_nine), "4153508888");
                commit();
                return null;
            }
        };
    }

    private static final ArrayList<ContentProviderOperation> ops = new ArrayList<>();

    private static void addContact(final String name, final Drawable d, final String phone) {
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageArray = stream.toByteArray();
        Log.d("Contact", "Completed Loading " + name);

        final int rawContactID = ops.size();

        // Adding insert operation to operations list
        // to insert a new raw contact in the table ContactsContract.RawContacts
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // Adding insert operation to operations list
        // to insert display name in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());

        // Adding insert operation to operations list
        // to  insert Home Phone Number in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .build());

        // Adding insert operation to operations list
        // Picture
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, imageArray)
                .build());
    }

    private static void commit() {
        try {
            LollipopExerciseApp.getAppContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }


/*
    public static void addContacts(final Context context) {
        addContact(context, "Aaron", "http://fc04.deviantart.net/fs71/f/2013/285/f/0/cartoon_cayby__new_avatar_profile__by_c_e_studio-d6q7znc.png");
        addContact(context, "Abby", "http://fc04.deviantart.net/fs71/i/2013/319/f/1/female_cartoon_avatar_by_ahninniah-d6fo72f.png");
        addContact(context, "Abel", "http://fc02.deviantart.net/fs71/i/2013/319/5/b/male_cartoon_avatar_by_ahninniah-d6ib8p2.png");
        addContact(context, "Abram", "https://cdnil0.fiverrcdn.com/deliveries/1526515/v2_680_459/create-cartoon-caricatures_ws_1389859304.png");
        addContact(context, "Ada", "https://cdnil1.fiverrcdn.com/deliveries/459030/large/create-cartoon-caricatures_ws_1364567168.png");
        addContact(context, "Adam", "http://i133.photobucket.com/albums/q41/Q_viola_Q/stars/Picture3.png");
        addContact(context, "Adda", "http://mommyjenna.com/wp-content/uploads/2013/03/profile_pic-300x300.png");
        addContact(context, "Addie", "http://www.deanmercado.com/wp-content/uploads/Dean-Mercado-Cartoon-Avatar.png");
        addContact(context, "Addison", "https://cdnil1.fiverrcdn.com/deliveries/274870/large/create-cartoon-caricatures_ws_1370419086.png");
    }

    private static List<String> sUrls = Collections.synchronizedList(new ArrayList<String>());
    private static AtomicInteger sCounter = new AtomicInteger(0);

    private static void addContact(final Context context, final String name, final String imageUrl) {
        sUrls.add(imageUrl);
        Picasso.with(context).load(imageUrl).into(new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.d("Contact", "Completed Loading " + name + " " + imageUrl);
                final ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                final int rawContactID = ops.size();
                final String phone = BASE_PHONE + sCounter.getAndIncrement();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] imageArray = stream.toByteArray();

                // Adding insert operation to operations list
                // to insert a new raw contact in the table ContactsContract.RawContacts
                ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build());

                // Adding insert operation to operations list
                // to insert display name in the table ContactsContract.Data
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                        .build());

                // Adding insert operation to operations list
                // to  insert Home Phone Number in the table ContactsContract.Data
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                        .build());


                // Picture
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, imageArray)
                        .build());

                // Executing all the insert operations as a single database transaction
                try {
                    context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                }

                didFinish(imageUrl);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Toast.makeText(LollipopExerciseApp.getAppContext(), errorDrawable.toString(), Toast.LENGTH_LONG).show();
                didFinish(imageUrl);
            }

            private void didFinish(final String imageUrl) {
                sUrls.remove(imageUrl);
                if (sUrls.size() == 0) {
                    sCounter.set(0);
//                    final Intent intent = new Intent(ACTION);
//                    LocalBroadcastManager.getInstance(context).sendBroadcastSync(intent);
                }
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }
    */
}
