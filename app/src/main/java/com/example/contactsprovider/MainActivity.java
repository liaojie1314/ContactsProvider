package com.example.contactsprovider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getUserInfo();
        checkContactReadPermission();
    }

    private void checkContactReadPermission() {
        int readContactPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);
        if (readContactPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CODE);
        }
    }

    public void getContactInfo(View view) {
        getUserInfo();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "has permission...");
            } else {
                Log.d(TAG, "no permission...");
            }
        }
    }

    private void getUserInfo() {
        ContentResolver cr = getContentResolver();
        //Uri constantsUri=Uri.parse("content://com.android.contacts");
        Uri rawContactUri = Uri.parse("content://" + ContactsContract.AUTHORITY + "/raw_contacts");

        Cursor rawContactCursor = cr.query(rawContactUri, new String[]{"contact_id", "display_name"}, null, null, null);
        //string[] columnNames = rawContactCursor.getColumnNames();
        List<UserInfo> userInfos = new ArrayList<>();

        while (rawContactCursor.moveToNext()) {
            UserInfo userInfo = new UserInfo();
            userInfo.setId(rawContactCursor.getString(rawContactCursor.getColumnIndex("contact_id")));
            userInfo.setDisplayName(rawContactCursor.getString(rawContactCursor.getColumnIndex("display_name")));
            userInfos.add(userInfo);
            //for (String columnName : columnNames) {
            //      Log.d(TAG, columnName + " values-->" + rawContactCursor.getString(rawContactCursor.getColumnIndex(columnName)));
            // }
        }

        rawContactCursor.close();
        Uri phoneUri = Uri.parse("content://" + ContactsContract.AUTHORITY + "/raw_contacts");
        for (UserInfo userInfo : userInfos) {
            Cursor phoneCursor = cr.query(phoneUri, new String[]{"data1"}, "raw_contact_id", new String[]{userInfo.getId()}, null);
            if (phoneCursor.moveToNext()) {
                userInfo.setPhoneNum(phoneCursor.getString(0).replace("-", ""));
            }

            phoneCursor.close();
            Log.d(TAG, "UserInfo-->" + userInfo);
        }
    }
}