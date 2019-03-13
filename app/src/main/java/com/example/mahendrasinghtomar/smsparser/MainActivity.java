package com.example.mahendrasinghtomar.smsparser;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED ||ContextCompat.checkSelfPermission(getApplicationContext(),android.Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS,android.Manifest.permission.READ_PHONE_STATE,android.Manifest.permission.READ_SMS,android.Manifest.permission.READ_CONTACTS}, 101);
            }
            NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()){
                Intent intent=new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
