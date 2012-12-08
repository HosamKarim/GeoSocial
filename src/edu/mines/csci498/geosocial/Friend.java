package edu.mines.csci498.geosocial;

import java.util.List;

public class Friend {

	String name;
	String phoneNumber;
	String status;
	boolean pending; 
	double lon,lat;
	
	public Friend(String name,String phone) {
		this.name = name;
		this.phoneNumber = phone;
		this.pending = true;
	}
	
	public Friend(String phone) {
		this.name = "(PENDING)";
		this.phoneNumber = phone;
		this.pending = true;
	}
	
	void setStatus(String newStatus) {
		this.status = newStatus;
	}
	
	public String getStatus() {
		return this.status;
	}
	
	public boolean isPending() {
		return this.pending;
	}
	
	public void setName(String name) {
		this.name = name; 
	}

	public String getName() {
		return this.name;
	}
	
	public String getPhone() {
		return this.phoneNumber;
	}
	
	public void setLongitude(double lon) {
		this.lon = lon;
	}
	
	public void setLatitude(double lat) {
		this.lat = lat;
	}
	
	public double getLongitude() {
		return this.lon;
	}
	
	public double getLatitude() {
		return this.lat;
	}
	
	public void confirmed() {
		this.pending=false; 
	}
	
}