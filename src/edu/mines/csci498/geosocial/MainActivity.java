package edu.mines.csci498.geosocial;

import static edu.mines.csci498.geosocial.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static edu.mines.csci498.geosocial.CommonUtilities.regId;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gcm.GCMRegistrar;

import edu.mines.csci498.geosocial.AllFriendsActivity.FriendsAdapter;
import edu.mines.csci498.geosocial.AllFriendsActivity.FriendsHolder;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	HandleMessageReceiver mHandleMessageReceiver = new HandleMessageReceiver();
	
	private AsyncTask<Void,Void,Void> mConfirmRequest;
	private AsyncTask<Void,Void,Void> mUpdateStatus;
	
	LocationManager locMgr;
	Location loc;
	
	List<Friend> friends; 
	List<Friend> nearByFriends = new ArrayList<Friend>(); 
	StatusAdapter adapter = null;
	
	float result[];
	
	double lon = 0 , lat = 0, diameter = 5000;
	
	private String newFriendName;
	private String newFriendNumber;
	
	EditText status;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        status = (EditText) findViewById(R.id.status);
       
        Button update = (Button) findViewById(R.id.update);
        update.setOnClickListener(onUpdate);
        registerReceiver(mHandleMessageReceiver,
                new IntentFilter(DISPLAY_MESSAGE_ACTION));
        
        if(getIntent().hasExtra("request")) {
    		
    			String message = getIntent().getExtras().getString("message");
    			newFriendName = getIntent().getStringExtra("friend");
    			newFriendNumber = getIntent().getStringExtra("number");
    			createAlertDialog(message);
        }
        
		locMgr = (LocationManager)getSystemService(LOCATION_SERVICE);
		loc = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, onLocationChange);
		
		friends = FriendList.getFriends();
        
		ListView list = (ListView)findViewById(R.id.list);
		adapter = new StatusAdapter();
		list.setAdapter(adapter);

	/** Application Crashed once trying to calculate nearby friends 	
		for(Friend f : friends) {
			Location.distanceBetween(lat, lon, f.getLatitude(), f.getLongitude(), result);
			if(result[0] <= diameter*1.60934) {
				nearByFriends.add(f);
			}	
		}
	*/	
        
    }
    
    private OnClickListener onUpdate = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(!status.getText().toString().equals("")) {
	    		lon = loc.getLongitude();
				lat = loc.getLatitude();
				
				sendUpdateStatus(GCMRegistrar.getRegistrationId(MainActivity.this),status.getText().toString(),Double.toString(lat),Double.toString(lon));
			} else {
				Toast.makeText(MainActivity.this, getString(R.string.status_blank), Toast.LENGTH_LONG).show();
			}
			
		}
	};
	
	LocationListener onLocationChange = new LocationListener() { 
		public void onLocationChanged(Location current) {
			lon = current.getLongitude();
			lat = current.getLatitude();
			
		}
		
		public void onProviderDisabled(String provider) {
			// required for interface, not used
		}
		public void onProviderEnabled(String provider) {
			// required for interface, not used
		}
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// required for interface, not used
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
			sendConfrimRequest(regId);
			Friend newFriend = new Friend(newFriendName,newFriendNumber);
			newFriend.confirmed();
			FriendList.addFriend(newFriend);
			
		}
    	
    }
    @Override
    public void onResume() {
    	super.onResume(); 

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	    
    	if(item.getItemId() == R.id.menu_send_request) {
    		
    		Intent menuRequest = new Intent(this,RequestActivity.class);
    		menuRequest.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
    		startActivity(menuRequest);
    		
    	}else if (item.getItemId() == R.id.register) {
    		Intent menuRegister = new Intent(this,RegisterActivity.class);
    		menuRegister.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    		Log.d("MainActivity", "Starting RegisterActivity");
    		startActivity(menuRegister);
    	}else if (item.getItemId() == R.id.menu_friends_list) {
    		Intent menuFriends = new Intent(this,AllFriendsActivity.class);
    		menuFriends.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    		
    		startActivity(menuFriends);
    	}else if (item.getItemId() == R.id.menu_location) {
    		lon = loc.getLongitude();
			lat = loc.getLatitude();

			Toast.makeText(this, "LON: " + lon + ", LAT: " + lat , Toast.LENGTH_LONG).show();
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
    
    private void sendUpdateStatus(final String regId, final String status, final String lat, final String lon ) { 

		final Context context = this;
		
		
        
        mUpdateStatus = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                 ServerUtilities.sendStatusUpdate(context, regId,status,lat,lon);
                        
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                
            }

        };
        mUpdateStatus.execute(null, null, null);
}
    
  //-- ADAPTER
  	class StatusAdapter extends ArrayAdapter<Friend> {
  		public StatusAdapter() {
  			super(MainActivity.this, R.layout.status_row, friends);
  		}
  		
  		@Override
  		public View getView(int position, View convertView, ViewGroup parent) {
  			View details = convertView;
  			StatusHolder holder = null;
  			
  			if (details == null) {
  				LayoutInflater inflater = getLayoutInflater();
  				details = inflater.inflate(R.layout.status_row, parent, false); 
  				holder = new StatusHolder(details); 
  				details.setTag(holder);
  			}
  			
  			else {
  				holder = (StatusHolder)details.getTag();
  			}
  			
  			holder.populateFrom(friends.get(position)); 
  			
  			return details;
  		} 
  	}
  	
    //-- HOLDER
   	class StatusHolder {
   		private TextView name = null;
   		private TextView status = null;

   		StatusHolder(View details) { 
   			name = (TextView)details.findViewById(R.id.nameStatus); 
   			status = (TextView)details.findViewById(R.id.statusView); 

   		}

   		void populateFrom(Friend friend) { 			
   			name.setText("" + friend.getName()); 
   			status.setText("" + friend.getStatus());
   			
   		}
   	}
    
}
