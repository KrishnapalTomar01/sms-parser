package com.example.mahendrasinghtomar.smsparser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class IncomingSms extends BroadcastReceiver {

    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();
    private GPSTracker gpsTracker;

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
                    if(message.equals("#101")) {

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
                    }
                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
    }
}
