package Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by bill on 11/5/17.
 */

public class ContactUtils {


    public static ArrayList<String> ContactNumber(Context con, String query) {

        ArrayList<String> tels = new ArrayList<>();

        if (MathUtils.isNumeric(query.replace(" ", ""))) {
            tels.add(query);
            return tels;

        }
        String name;
        Uri contentUri;
        final String id;
        contentUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, Uri.encode(query));
        ContentResolver cr = con.getContentResolver();
        Cursor cur = cr.query(contentUri, null, null, null, ContactsContract.Contacts.TIMES_CONTACTED + " DESC");
        // Log.i("query:",cr.);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            if (cur != null && cur.moveToNext()) {
                name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                Log.i("query:", name);
                id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));

                Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{id}, null);
                while (pCur != null && pCur.moveToNext()) {
                    String phone = pCur.getString(
                            pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    tels.add(phone);
                }
                if (pCur != null) {
                    pCur.close();
                }

            }
        }
        return tels;
    }


    public static ArrayList<String> ContactMail(Context con, String query) {
        String name;

        ArrayList<String> emails = new ArrayList<>();
        Uri contentUri;
        final String id;
        contentUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, Uri.encode(query));
        ContentResolver cr = con.getContentResolver();
        Cursor cur = cr.query(contentUri, null, null, null, ContactsContract.Contacts.TIMES_CONTACTED + " DESC");
        if ((cur != null ? cur.getCount() : 0) > 0) {
            if (cur != null && cur.moveToNext()) {
                name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));


                Cursor emailCur = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{id}, null);

                if (emailCur != null) {
                    while (emailCur.moveToNext()) {
                        // This would allow you get several email addresses
                        // if the email addresses were stored in an array
                        String email = emailCur.getString(
                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        String emailType = emailCur.getString(
                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                        emails.add(email);

                    }
                }
                if (emailCur != null) {
                    emailCur.close();
                }


            }
        }
        return emails;
    }


}

