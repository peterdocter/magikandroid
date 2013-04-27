/**
 * 
 */
package com.example.magik.monitoring;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

/**
 * @author User
 *
 */
public class Connectivity extends Activity{
	
	private boolean connected;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		connected = false;
	}		
	
	public boolean connected()
	{
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
	    connected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
	    return connected;
	}
}
