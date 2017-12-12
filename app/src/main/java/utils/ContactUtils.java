package utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bill on 11/5/17.
 */

public class ContactUtils {


    private static final String TAG = "ContactUtils";
    public static ArrayList<String> ContactNumber(String query, Context context) {
        Log.i(TAG, "the text from user is " + query);
        ArrayList<String> tels = new ArrayList<>();

        if (MathUtils.isNumeric(query.replace(" ", ""))) {
            Log.i(TAG, "the user told a number");
            tels.add(query);
            return tels;
        }
        //String for temporary name
        String name;
        //hash map for matched name;
        HashMap<String, Double> selname;
        //String for  id of contact
        String id;
        //list with strings of all contact names
        List<String> names = new ArrayList<>();

        Uri contentUri = ContactsContract.Contacts.CONTENT_URI;
        ContentResolver cr = context.getContentResolver();

        Cursor cur = cr.query(contentUri, null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    names.add(name);
                }
            }

            if (cur != null)
                cur.close();
        }
        Log.i(TAG, "list's size of contact names " + names.size());


        selname = SearchStringHelper.getBestStringMatch(names, query);
        Log.i(TAG, "matched contact is " + selname);


        //if the contact name has phone number get all existed phone numbers
        if (!selname.containsKey("no_contact")) {
            Map.Entry<String, Double> matchedName = selname.entrySet().iterator().next();
            Log.i(TAG, String.valueOf(matchedName));

            @SuppressLint("Recycle")
            Cursor pCur = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?",
                    new String[]{matchedName.getKey()}, null
            );

            while (pCur != null && pCur.moveToNext()) {

                String phone = pCur.getString(
                        pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                );
                tels.add(phone);
                Log.i(TAG, "phone number is " + phone);
            }
            if (pCur != null)
                pCur.close();
        }

        return tels;
    }

    public static ArrayList<String> ContactMail(String query, Context context) {
        String name;

        ArrayList<String> emails = new ArrayList<>();
        Uri contentUri;
        final String id;
        contentUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, Uri.encode(query));
        ContentResolver cr = context.getContentResolver();
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

