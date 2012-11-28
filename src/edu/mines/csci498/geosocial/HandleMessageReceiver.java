package edu.mines.csci498.geosocial;
import static edu.mines.csci498.geosocial.CommonUtilities.EXTRA_MESSAGE;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static edu.mines.csci498.geosocial.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static edu.mines.csci498.geosocial.CommonUtilities.EXTRA_MESSAGE;

public class HandleMessageReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
    	String message = intent.getExtras().getString(EXTRA_MESSAGE);
    	
    	if(intent.hasExtra("type")) {
    		if(intent.getStringExtra("type").equals("Alert")) {
    			Intent friendReq = new Intent(context,MainActivity.class);
    			friendReq.putExtra("request", "friend");
    			friendReq.putExtra("message", message);
    			
    			friendReq.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
    			
    			context.startActivity(friendReq);
    		}
    	} else {
    	
    		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        //mDisplay.append(newMessage + "\n");
    	}
		
	}

}
