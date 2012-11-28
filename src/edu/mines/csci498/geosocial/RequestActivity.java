/*NOTE: Usage of Pre-existing code provided by Google Inc. Demonstrating the usage of GCM from mobile device 
 * The code has been modified for the use for the GeoSocial App  
 */
package edu.mines.csci498.geosocial;

import static edu.mines.csci498.geosocial.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static edu.mines.csci498.geosocial.CommonUtilities.EXTRA_MESSAGE;

import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RequestActivity extends Activity{
	
	EditText friendNumber; 
	TextView mDisplay;
	AsyncTask<Void,Void,Void> mSendRequest; 
	HandleMessageReceiver mHandleMessageReceiver = new HandleMessageReceiver();
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_activity);
        
        friendNumber = (EditText) findViewById(R.id.friendNumber);
        mDisplay = (TextView) findViewById(R.id.requestDisplay);
        Button send = (Button) findViewById(R.id.send);
        send.setOnClickListener(onSend);
        
        registerReceiver(mHandleMessageReceiver,
                new IntentFilter(DISPLAY_MESSAGE_ACTION));
                
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.request_options, menu);
        return true;
    }
    
    private View.OnClickListener onSend = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			if(!friendNumber.getText().toString().equals("")) {
				final Context context = RequestActivity.this;
				//ServerUtilities.sendFriendRequest(context,GCMRegistrar.getRegistrationId(RequestActivity.this), friendNumber.getText().toString());
				sendRequest(GCMRegistrar.getRegistrationId(RequestActivity.this),friendNumber.getText().toString());
				//finish();
			} else {
				String message = "Friend's Number Field Must be Filled out";
				Toast.makeText(RequestActivity.this, message, Toast.LENGTH_LONG).show();
				
			}
			
		}
	};
	
	 private void sendRequest(final String regId, final String fNumber) {
	        // Try to register again, but not in the UI thread.
	        // It's also necessary to cancel the thread onDestroy(),
	        // hence the use of AsyncTask instead of a raw thread.
	    		final Context context = this;
	    		//boolean registered = ServerUtilities.register(context, regId, name, number);
	    		
		        
		        mSendRequest = new AsyncTask<Void, Void, Void>() {
		
		            @Override
		            protected Void doInBackground(Void... params) {
		                 ServerUtilities.sendFriendRequest(context, regId, fNumber);
		                        
		                return null;
		            }
		
		            @Override
		            protected void onPostExecute(Void result) {
		            	mSendRequest = null;
		                
		            }
		
		        };
		        mSendRequest.execute(null, null, null);
	    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if(item.getItemId() == R.id.menu_main){
    		startActivity(new Intent(this, MainActivity.class));
    	}
    	
    	return super.onOptionsItemSelected(item);
    }
    

}
