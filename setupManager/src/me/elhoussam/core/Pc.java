package me.elhoussam.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.elhoussam.interfaces.infoInterface;

public class Pc {
	private String ipAddress = "unkown";
	private infoInterface ref = null ;
	private Boolean pcState = true ;
	private Date lastconnection = null ;
	public String getIpAddress() {
		return ipAddress;
	}
	public Pc(String ipAddress, infoInterface ref, Date lastconnection) {
		super();
		this.ipAddress = ipAddress;
		this.ref = ref; 
		this.lastconnection = lastconnection;
	}
	public Pc() {}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public infoInterface getRef() {
		return ref;
	}
	public void setRef(infoInterface ref) {
		this.ref = ref;
	}
	public Boolean getPcState() {
		return pcState;
	}
	public void setPcState(Boolean pcState) {
		this.pcState = pcState;
	}
	public String getLastconnection() {

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return dateFormat.format( this.lastconnection );
	} 
	public void updateLastconnection() {
		Date date = new Date();
		this.lastconnection  = date ;		
	}
}
