package com.example.mahendrasinghtomar.smsparser;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class IncomingSms extends BroadcastReceiver {

    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();
    private GPSTracker gpsTracker;

    public List<Contacts> getList(Context context){
        List<Contacts> contactList=new ArrayList<>();
        ContentResolver cr=context.getContentResolver();
        Cursor cursor=cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");
        if (cursor.getCount()> 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name=cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                Contacts info = new Contacts(id,name,cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                contactList.add(info);

                }

            cursor.close();
        }
        return contactList;
    }

    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);


                    // Show Alert
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context,
                            "senderNum: " + senderNum + ", message: " + message, duration);
                    toast.show();

                    if(message.trim().equals("#101")) {

                        double latitude=0,longitude=0;
                        gpsTracker = new GPSTracker(context);
                        if(gpsTracker.canGetLocation()){
                            latitude = gpsTracker.getLatitude();
                            longitude = gpsTracker.getLongitude();
                        }else{
                            gpsTracker.showSettingsAlert();
                        }
                        SmsManager smsManager = SmsManager.getDefault();
                        StringBuffer smsBody = new StringBuffer();
                        smsBody.append("http://maps.google.com/maps?q=");
                        smsBody.append(latitude);
                        smsBody.append(",");
                        smsBody.append(longitude);
                        smsManager.sendTextMessage(senderNum, null, smsBody.toString(), null, null);
                    } else if(message.startsWith("#102")){

                        String str = message.substring(5);
                        str=str.trim();
                        List <Contacts> contactList=getList(context);
                        for(Contacts c:contactList){
                            if(c.getName().equalsIgnoreCase(str)){
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(senderNum, null,"Name: "+c.getName()+"\n"+"Number is: "+c.getPhone(), null, null);
                                Toast.makeText(context,"Name: "+c.getName()+"\n"+"Number is: "+c.getPhone(),Toast.LENGTH_LONG).show();
                                break;
                            }
                        }
                    }
                    else if(message.trim().equals("#103")){
                        AudioManager am=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
                        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        Toast.makeText(context,"Audio profile chnaged",Toast.LENGTH_LONG).show();
                    }
                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
    }
}
