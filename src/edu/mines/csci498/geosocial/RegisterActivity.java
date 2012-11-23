package edu.mines.csci498.geosocial;


import static edu.mines.csci498.geosocial.CommonUtilities.EXTRA_MESSAGE;
import static edu.mines.csci498.geosocial.CommonUtilities.SENDER_ID;
import static edu.mines.csci498.geosocial.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static edu.mines.csci498.geosocial.CommonUtilities.SENDER_ID;
import static edu.mines.csci498.geosocial.CommonUtilities.SERVER_URL;

//import com.example.gcmtest.R;
//import com.example.gcmtest.ServerUtilities;
//import com.example.gcmtest.R;
import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class RegisterActivity extends Activity{
	
    TextView mDisplay;
    AsyncTask<Void, Void, Void> mRegisterTask;
    String registerId; 
    String name;
    String number;
    boolean isGCMRegistered = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d("RegisterActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        
       
        checkNotNull(SERVER_URL, "SERVER_URL");
        checkNotNull(SENDER_ID, "SENDER_ID");
        
        GCMRegistrar.checkDevice(this);// Make sure the device has the proper dependencies.
        
    	Log.d("RegisterActivity", "Proper dependencies Check");
        
        GCMRegistrar.checkManifest(this);// Make sure the manifest was properly set - comment out this line
        								// while developing the app, then uncomment it when it's ready.
        
        Log.d("RegisterActivity", "Manifest Check");
        
        mDisplay = (TextView) findViewById(R.id.display);
        registerReceiver(mHandleMessageReceiver,
                new IntentFilter(DISPLAY_MESSAGE_ACTION));
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(this, SENDER_ID);
        } else {
            // Device is already registered on GCM, check server.
        	isGCMRegistered = true;
        	registerId = regId; 
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
                mDisplay.append(getString(R.string.already_registered) + "\n");
            }          
            
        }
        
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.register_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            /*
             * Typically, an application registers automatically, so options
             * below are disabled. Uncomment them if you want to manually
             * register or unregister the device (you will also need to
             * uncomment the equivalent options on options_menu.xml).
             */
            
            case R.id.options_register:
                GCMRegistrar.register(this, SENDER_ID);
                name = "Robert";
                number = "911";
                registerOnServer(registerId,name,number);
                return true;
            case R.id.options_unregister:
                GCMRegistrar.unregister(this);
                return true;
            case R.id.options_clear:
                mDisplay.setText(null);
                return true;
            case R.id.options_exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        unregisterReceiver(mHandleMessageReceiver);
        GCMRegistrar.onDestroy(this);
        super.onDestroy();
    }

    private void checkNotNull(Object reference, String name) {
        if (reference == null) {
            throw new NullPointerException(
                    getString(R.string.error_config, name));
        }
    }
    
    private void registerOnServer(final String regId, final String name, final String number) {
        // Try to register again, but not in the UI thread.
        // It's also necessary to cancel the thread onDestroy(),
        // hence the use of AsyncTask instead of a raw thread.
    	if(isGCMRegistered) {
	        final Context context = this;
	        mRegisterTask = new AsyncTask<Void, Void, Void>() {
	
	            @Override
	            protected Void doInBackground(Void... params) {
	                boolean registered = ServerUtilities.register(context, regId, name, number);
	                        
	                // At this point all attempts to register with the app
	                // server failed, so we need to unregister the device
	                // from GCM - the app will try to register again when
	                // it is restarted. Note that GCM will send an
	                // unregistered callback upon completion, but
	                // GCMIntentService.onUnregistered() will ignore it.\
	    			
	                if (!registered) {
	                    GCMRegistrar.unregister(context);
	                }
	                return null;
	            }
	
	            @Override
	            protected void onPostExecute(Void result) {
	                mRegisterTask = null;
	            }
	
	        };
	        mRegisterTask.execute(null, null, null);
    	}
    }

    private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            mDisplay.append(newMessage + "\n");
        }
    };

}
