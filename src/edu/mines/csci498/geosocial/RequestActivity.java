package edu.mines.csci498.geosocial;

import static edu.mines.csci498.geosocial.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static edu.mines.csci498.geosocial.CommonUtilities.EXTRA_MESSAGE;

import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
				ServerUtilities.sendFriendRequest(context,GCMRegistrar.getRegistrationId(RequestActivity.this), friendNumber.getText().toString());
				//finish();
			} else {
				String message = "Friend's Number Field Must be Filled out";
				Toast.makeText(RequestActivity.this, message, Toast.LENGTH_LONG).show();
				
			}
			
		}
	};
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if(item.getItemId() == R.id.menu_main){
    		startActivity(new Intent(this, MainActivity.class));
    	}
    	
    	return super.onOptionsItemSelected(item);
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
