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

public class FriendList {        

	static final List<Friend> friends = new ArrayList<Friend>();
	
	private FriendList() {
		//Static factory method 
	}
	
	public static List<Friend> getFriends(){
		return new ArrayList<Friend>(friends);
	}
	
	public static void addFriend(Friend f) {
		friends.add(f);
	}
	
	public static void removeFriend(Friend f) {
		friends.remove(f);
	}
	
	public static void friendConfirmed(String name, String number) {
		for(Friend f : friends) {
			if( f.getPhone().equals(number)) {
				f.setName(name);
				f.confirmed();				
				return;
			}
		}
	}
	
	public Friend findFriendByName(String name) {
		for(Friend f : friends) {
			if(f.getName().toString().equals(name)) {
				return f;
			}
		}
		return null;
	}
	


}