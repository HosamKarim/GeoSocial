package edu.mines.csci498.geosocial;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if(getIntent().hasExtra("request")) {
    		
    			String message = getIntent().getExtras().getString("message");
    			createAlertDialog(message);
        }
        
    }
    
    private void createAlertDialog(String message) {

    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);	
    	
    	alertDialogBuilder.setTitle("New Friend Request")
    					  .setMessage(message);
    	
    	AlertDialog alertDialog = alertDialogBuilder.create();
    	
    	alertDialog.show();
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
    		startActivity(new Intent(this, RequestActivity.class));
    	}else if (item.getItemId() == R.id.register){
    		Log.d("MainActivity", "Starting RegisterActivity");
    		startActivity(new Intent(this, RegisterActivity.class));
    	}
    	
    	
    	return super.onOptionsItemSelected(item);
    }
}
