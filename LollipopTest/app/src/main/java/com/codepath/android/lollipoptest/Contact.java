package com.codepath.android.lollipoptest;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.content.CursorLoader;

import java.io.Serializable;
import java.util.ArrayList;

public class Contact implements Serializable {
    public String id;
    public String name;
    public String thumbnailUri;
    public ArrayList<ContactEmail> emails;
    public ArrayList<ContactPhone> numbers;

    public Contact(String id, String thumbnailUri, String name) {
        this.id = id;
        this.name = name;
        this.thumbnailUri = thumbnailUri;
        this.emails = new ArrayList<ContactEmail>();
        this.numbers = new ArrayList<ContactPhone>();
    }

    public void addEmail(String address, String type) {
        emails.add(new ContactEmail(address, type));
    }

    public void addNumber(String number, String type) {
        numbers.add(new ContactPhone(number, type));
    }

    public void fetchContactNumbers(final Context context) {
        if (numbers.size() != 0) return;
        /* Get numbers */
        final String[] numberProjection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE, };
        Cursor phone = new CursorLoader(context, ContactsContract.CommonDataKinds.Phone.CONTENT_URI, numberProjection,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ?",
                new String[] { String.valueOf(id) },
                null).loadInBackground();

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

    public void fetchContactEmails(final Context context) {
        if (emails.size() != 0) return;
        // Get email
        final String[] emailProjection = new String[] { ContactsContract.CommonDataKinds.Email.DATA, ContactsContract.CommonDataKinds.Email.TYPE };

        Cursor email = new CursorLoader(context, ContactsContract.CommonDataKinds.Email.CONTENT_URI, emailProjection,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + "= ?",
                new String[] { String.valueOf(id) },
                null).loadInBackground();

        if (email.moveToFirst()) {
            final int contactEmailColumnIndex = email.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
            final int contactTypeColumnIndex = email.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE);

            while (!email.isAfterLast()) {
                final String address = email.getString(contactEmailColumnIndex);
                final int type = email.getInt(contactTypeColumnIndex);
                String customLabel = "Custom";
                CharSequence emailType =
                        ContactsContract.CommonDataKinds.Email.getTypeLabel(
                                context.getResources(), type, customLabel);
                addEmail(address, emailType.toString());
                email.moveToNext();
            }

        }

        email.close();
    }
}
