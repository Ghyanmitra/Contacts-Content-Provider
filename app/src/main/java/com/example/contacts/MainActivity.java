package com.example.contacts;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

//    private TextView textView;
//    private EditText edtText;
//    private Button btnSearch;

    private EditText edtTxtName, edtTxtNumber;
    private Spinner spinner;
    private Button btnAddContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        textView = (TextView) findViewById(R.id.textView);
//        edtText = (EditText) findViewById(R.id.edtText);
//        btnSearch = (Button) findViewById(R.id.btnSearch);

        edtTxtName = (EditText) findViewById(R.id.edtTxtName);
        edtTxtNumber = (EditText) findViewById(R.id.edtTxtNumber);
        spinner = (Spinner) findViewById(R.id.spinner);
        btnAddContact = (Button) findViewById(R.id.btnAddContact);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_CONTACTS}, 1);
        }else {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_CONTACTS}, 2);
            }else {
//                deleteContact(4);
                update(5);
            }

        }

        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertContact();
            }
        });

//        btnSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                searchByNumber();
//                searchByName();
//            }
//        });
    }

    private void getAllContacts() {
        String[] projection = new String[] {ContactsContract.Data._ID,
                ContactsContract.Data.DISPLAY_NAME_PRIMARY,
                ContactsContract.Data.DATA1,
                ContactsContract.Data.MIMETYPE};

        String selection = ContactsContract.Data.MIMETYPE + "=?";
        Log.d(TAG, "onCreate: mimeType: " + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        String[] selectionArg = new String[] {ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};

        Cursor cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI, projection, selection, selectionArg, null);
        if (null != cursor) {
            if (cursor.moveToFirst()) {
                String text = "";

                for (int j=0; j<cursor.getCount(); j++) {
                    for (int i=0; i<cursor.getColumnCount(); i++) {
                        text += cursor.getColumnName(i) + ": " + cursor.getString(i)+ "\n";
                    }

                    text += "*************\n";

                    cursor.moveToNext();
                }
                Log.d(TAG, "onCreate: text: " + text);
//                    textView.setText(text);
            }
            cursor.close();
        }
    }

    private void searchByNumber () {
        Log.d(TAG, "searchByNumber: started");
//        String input = edtText.getText().toString();

        String input = "";

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, input);

        String[] projection = new String[] {ContactsContract.PhoneLookup._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME_PRIMARY,
                ContactsContract.PhoneLookup.NUMBER};

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (null != cursor) {
            String text = "";
            if (cursor.moveToFirst()){
                for (int i=0; i<cursor.getCount(); i++) {
                    for (int j=0; j<cursor.getColumnCount(); j++) {
                        text += cursor.getColumnName(j) + ": " + cursor.getString(j)+ "\n";
                    }

                    text += "**********";
                    cursor.moveToNext();
                }
            }

            cursor.close();
//            textView.setText(text);
        }else {
//            textView.setText("There is no such contact");
        }
    }

    private void searchByName () {
//        String name = edtText.getText().toString();
        String name = "";
        String[] projection = new String[] {ContactsContract.Data._ID, ContactsContract.Data.DISPLAY_NAME_PRIMARY,
                                            ContactsContract.Data.DATA1, ContactsContract.Data.RAW_CONTACT_ID};

        String selection = ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.Data.DISPLAY_NAME_PRIMARY + "=?";
        String[] selectionArg = new String[] {ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, name};

        Cursor cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI, projection, selection, selectionArg, null);
        if (null != cursor) {
            String text = "";
            if (cursor.moveToFirst()){
                for (int i=0; i<cursor.getCount(); i++) {
                    for (int j=0; j<cursor.getColumnCount(); j++) {
                        text += cursor.getColumnName(j) + ": " + cursor.getString(j)+ "\n";
                    }

                    text += "**********";
                    cursor.moveToNext();
                }
            }

            cursor.close();
//            textView.setText(text);
        }else {
//            textView.setText("There is no such contact");
        }
    }

    private void deleteContact (long id) {
        int deletedRows = getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI,
                ContactsContract.RawContacts._ID + "=?", new String[] {String.valueOf(id)});
        Log.d(TAG, "deleteContact: deleted rows: " + deletedRows);
    }

    private void update (long id) {

        ContentValues values = new ContentValues();
        values.put(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY, "Sarah");

        int affectedRows = getContentResolver().update(ContactsContract.RawContacts.CONTENT_URI, values,
                ContactsContract.RawContacts._ID + "=?", new String[] {String.valueOf(id)});
        Log.d(TAG, "update: affected rows: " + affectedRows);
    }

    private void insertContact () {
        Log.d(TAG, "insertContact: started");
        String name = edtTxtName.getText().toString();
        String number = edtTxtNumber.getText().toString();
        String type = spinner.getSelectedItem().toString();

        if (!name.equals("") && !number.equals("")) {
            Uri uri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, new ContentValues());
            long id = ContentUris.parseId(uri);

            ContentValues nameValues = new ContentValues();
            nameValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            nameValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
            nameValues.put(ContactsContract.Data.RAW_CONTACT_ID, id);
            getContentResolver().insert(ContactsContract.Data.CONTENT_URI, nameValues);

            ContentValues numberValues = new ContentValues();
            numberValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            numberValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, number);
            if (type.equalsIgnoreCase("home")) {
                numberValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
            }else if(type.equalsIgnoreCase("mobile")) {
                numberValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            }else if (type.equalsIgnoreCase("work")) {
                numberValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
            }
            numberValues.put(ContactsContract.Data.RAW_CONTACT_ID, id);
            Uri newlyAddedContact = getContentResolver().insert(ContactsContract.Data.CONTENT_URI, numberValues);
            Log.d(TAG, "insertContact: uri: " + newlyAddedContact);
        }
    }
}
