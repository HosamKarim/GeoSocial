package edu.mines.csci498.geosocial;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
	import android.widget.TextView;

public class AllFriendsActivity extends Activity {
	
	
	List<Friend> friends;
	FriendsAdapter adapter = null;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		////TESTING
		//FriendList.addFriend(new Friend("Wahoo", "6348746"));
		//FriendList.addFriend(new Friend("Wahoo", "6348746"));
		
		friends = FriendList.getFriends();
		setContentView(R.layout.friends_list);
		ListView list = (ListView)findViewById(R.id.list);
		adapter = new FriendsAdapter();
		list.setAdapter(adapter);
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.friends_options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		startActivity(new Intent(this, MainActivity.class));
		return super.onOptionsItemSelected(item);
	}
	 //-- ADAPTER
	class FriendsAdapter extends ArrayAdapter<Friend> {
		public FriendsAdapter() {
			super(AllFriendsActivity.this, R.layout.friend_detail, friends);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View details = convertView;
			FriendsHolder holder = null;
			
			if (details == null) {
				LayoutInflater inflater = getLayoutInflater();
				details = inflater.inflate(R.layout.friend_detail, parent, false); 
				holder = new FriendsHolder(details); 
				details.setTag(holder);
			}
			
			else {
				holder = (FriendsHolder)details.getTag();
			}
			
			holder.populateFrom(friends.get(position)); 
			
			return details;
		} 
	}

	 //-- HOLDER
	class FriendsHolder {
		private TextView name = null;
		private TextView phone = null;

		FriendsHolder(View details) { 
			name = (TextView)details.findViewById(R.id.nameView); 
			phone = (TextView)details.findViewById(R.id.phoneView); 

		}

		void populateFrom(Friend friend) { 			
			name.setText("" + friend.getName()); 
			phone.setText("" + friend.getPhone());
		}
	}

}
