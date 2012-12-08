/*NOTE: Usage of Pre-existing code provided by Google Inc. Demonstrating the usage of GCM from mobile device 
 * The code has been modified for the use for the GeoSocial App  
 */

package edu.mines.csci498.geosocial;


import android.content.Context;
/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static  edu.mines.csci498.geosocial.CommonUtilities.SENDER_ID;
import static edu.mines.csci498.geosocial.CommonUtilities.displayMessage;
import static edu.mines.csci498.geosocial.CommonUtilities.displayAlert;
import static edu.mines.csci498.geosocial.CommonUtilities.REQUEST_MESSAGE;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

import edu.mines.csci498.geosocial.MainActivity;
import edu.mines.csci498.geosocial.R;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

    @SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device GCM registered: regId = " + registrationId);
        displayMessage(context, getString(R.string.gcm_registered));
        //ServerUtilities.register(context, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device GCM unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            //ServerUtilities.unregister(context, registrationId);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        String message = getString(R.string.gcm_message);
        CommonUtilities.displayMessage(context, "MESSAGE RECIEVED FROM SERVER");
        if(intent.hasExtra(REQUEST_MESSAGE)){
        	if(intent.getStringExtra(REQUEST_MESSAGE).equals("friend")) {
        		message = intent.getStringExtra("user") + " Wants to be Friends";
        		generateNotification(context, message, intent);
        	} else if(intent.getStringExtra(REQUEST_MESSAGE).equals("confirm")) {
        		CommonUtilities.displayMessage(context, "SERVER MESSAGE FRIEND CONFIRMATION FROM: "+intent.getStringExtra("user") +" "+ intent.getStringExtra("number"));
        		FriendList.friendConfirmed(intent.getStringExtra("user"), intent.getStringExtra("number"));
        		
        	}
        }
        
        //displayAlert(context, message);
        //displayMessage(context, message);
        // notifies user
        
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        //generateNotification(context, message, intent);
        displayMessage(context, message);

    }

    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message,Intent intent) {
        int icon = R.drawable.ic_stat_gcm;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        
        if(intent.hasExtra(REQUEST_MESSAGE)) {
        	notificationIntent.putExtra("request", "friend");
        	notificationIntent.putExtra("message", message );
        	notificationIntent.putExtra("friend", intent.getStringExtra("user"));
        	notificationIntent.putExtra("number", intent.getStringExtra("number"));
        }
        
        PendingIntent pendIntent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, pendIntent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

}