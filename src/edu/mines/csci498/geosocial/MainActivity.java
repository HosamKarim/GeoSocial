package edu.mines.csci498.geosocial;

import static edu.mines.csci498.geosocial.CommonUtilities.DISPLAY_MESSAGE_ACTION;

import com.google.android.gcm.GCMRegistrar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	HandleMessageReceiver mHandleMessageReceiver = new HandleMessageReceiver();
	
	private AsyncTask<Void,Void,Void> mConfirmRequest;
	private AsyncTask<Void,Void,Void> mUpdateStatus;
	EditText status; 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        status = (EditText) findViewById(R.id.status);
        Button update = (Button) findViewById(R.id.update);
        
        registerReceiver(mHandleMessageReceiver,
                new IntentFilter(DISPLAY_MESSAGE_ACTION));
        
        if(getIntent().hasExtra("request")) {
    		
    			String message = getIntent().getExtras().getString("message");
    			createAlertDialog(message);
        }

        
    }
    
    private OnClickListener onUpdate = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(!status.getText().toString().equals("")) {
				sendUpdateStatus(GCMRegistrar.getRegistrationId(MainActivity.this),status.getText().toString());
			} else {
				Toast.makeText(MainActivity.this, getString(R.string.status_blank), Toast.LENGTH_LONG).show();
			}
			
		}
	};
    
    private void createAlertDialog(String message) {

    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);	
    	
    	alertDialogBuilder.setTitle("New Friend Request")
    					  .setMessage(message)
    					  .setPositiveButton("Confirm", new confirmOnClickListener());
    	
    	AlertDialog alertDialog = alertDialogBuilder.create();
    	
    	alertDialog.show();
    }
    
    private final class confirmOnClickListener implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			sendConfrimRequest(GCMRegistrar.getRegistrationId(MainActivity.this));
			
		}
    	
    }
    @Override
    public void onResume() {
    	super.onResume(); 
    	/*
    	if(getIntent().hasExtra("request")) {
    		String message = getIntent().getExtras().getString("message");
    		createAlertDialog(message);
    	}
    	*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	    
    	if(item.getItemId() == R.id.menu_send_request){
    		
    		Intent menuRequest = new Intent(this,RequestActivity.class);
    		menuRequest.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
    		startActivity(menuRequest);
    		
    	}else if (item.getItemId() == R.id.register){
    		Intent menuRegister = new Intent(this,RegisterActivity.class);
    		menuRegister.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    		Log.d("MainActivity", "Starting RegisterActivity");
    		startActivity(menuRegister);
    	}
    	
    	
    	return super.onOptionsItemSelected(item);
    }
    
    private void sendConfrimRequest(final String regId ) { 

    		final Context context = this;
    		
    		
	        
	        mConfirmRequest = new AsyncTask<Void, Void, Void>() {
	
	            @Override
	            protected Void doInBackground(Void... params) {
	                 ServerUtilities.sendConfirmRequest(context, regId);
	                        
	                return null;
	            }
	
	            @Override
	            protected void onPostExecute(Void result) {
	            	Toast.makeText(context, "Confirm Request Sent!", Toast.LENGTH_LONG).show();
	                
	            }
	
	        };
	        mConfirmRequest.execute(null, null, null);
    }
    
    private void sendUpdateStatus(final String regId, final String status ) { 

		final Context context = this;
		
		
        
        mUpdateStatus = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                 ServerUtilities.sendStatusUpdate(context, regId,status);
                        
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
            	Toast.makeText(context, "Status Udpate Sent to Server!", Toast.LENGTH_LONG).show();
                
            }

        };
        mUpdateStatus.execute(null, null, null);
}
    
    
}
