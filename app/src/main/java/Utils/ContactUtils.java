package Utils;

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


    public static ArrayList<String> ContactNumber(String query, Context context) {

        ArrayList<String> tels = new ArrayList<>();

        if (MathUtils.isNumeric(query.replace(" ", ""))) {
            tels.add(query);
            return tels;

        }
        String name = null;
        HashMap<String, Double> selname;
        Uri contentUri;
        String id;
        List<String> names = new ArrayList<>();
        contentUri = ContactsContract.Contacts.CONTENT_URI;
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

            cur.close();
        }

        Log.i("name:", query);
        Log.i("name:", String.valueOf(names.size()));

        selname = SearchStringHelper.getBestStringMatch(names, query);
        Log.i("selfname:", String.valueOf(selname));

        if (!selname.containsKey("no_contact")) {
            Map.Entry<String, Double> selname1 = selname.entrySet().iterator().next();

            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?",
                    new String[]{selname1.getKey()}, null);
            while (pCur != null && pCur.moveToNext()) {
                String phone = pCur.getString(
                        pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                tels.add(phone);
            }
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

