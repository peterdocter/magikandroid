/**
 * 
 */
package com.magik.activities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.magik.R;

/**
 * This class is the input processing of the settings user interface.
 * @author User
 *
 */
public class SettingsActivity extends Activity {	
	
	private CheckBox checkWeb;
	
	private CheckBox checkFiles;
	
	private CheckBox checkAct;
	
	private CheckBox checkGyro;
	
	private CheckBox checkSync;

	
	private InterfaceManager interfaceManager;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		checkWeb = (CheckBox)findViewById(R.id.checkWeb);
		checkFiles = (CheckBox)findViewById(R.id.checkFiles);
		checkAct = (CheckBox)findViewById(R.id.checkAct);
		checkGyro = (CheckBox)findViewById(R.id.checkGyro);
		checkSync = (CheckBox)findViewById(R.id.checkSync);
		if(!connected())
		{
			checkSync.setEnabled(false);
		}
		interfaceManager = InterfaceManager.getInstance();
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		interfaceManager = InterfaceManager.getInstance();
		checkWeb.setChecked(interfaceManager.ismWeb());
		checkFiles.setChecked(interfaceManager.ismFiles());
		checkAct.setChecked(interfaceManager.ismAct());
		checkGyro.setChecked(interfaceManager.ismGyro());
		checkSync.setChecked(interfaceManager.ismSync());
		if(!connected())
		{
			checkSync.setEnabled(false);
			checkSync.setChecked(false);
		}
		super.onResume();
	}

	public void onCheckboxClicked(View view)
	{
		boolean checked = ((CheckBox)view).isChecked();
		if(interfaceManager.getContext( )==null)
		{
		   interfaceManager.setContext( getApplicationContext( ) ); 
		}
		switch (view.getId()) {
		case R.id.checkWeb:
			if(checked)
			{
				interfaceManager.setmWeb(true);
				startService(interfaceManager.getIntent( ));
			}
			else
			{
				interfaceManager.setmWeb(false);
				stopService(interfaceManager.getIntent( ));
			}
			
			break;
		case R.id.checkFiles:
			if(checked)
			{
				interfaceManager.setmFiles(true);
				startService( interfaceManager.getIntent( ) );
			}
			else
			{
				interfaceManager.setmFiles(false);
				stopService( interfaceManager.getIntent( ) );
			}			
			break;
		case R.id.checkAct:
			if(checked)
			{
				interfaceManager.setmAct(true);
				Toast.makeText(getApplicationContext(), "Swipe monitoring activated...", Toast.LENGTH_LONG).show();
			}
			else
			{
				interfaceManager.setmAct(false);
				Toast.makeText(getApplicationContext(), "Swipe monitoring stopped...", Toast.LENGTH_LONG).show();
			}			
			break;
		case R.id.checkGyro:
			if(checked)
			{
				interfaceManager.setmGyro(true);
				Toast.makeText(getApplicationContext(), "Device's orientation monitoring activated...", Toast.LENGTH_LONG).show();
			}
			else
			{
				interfaceManager.setmGyro(false);
				Toast.makeText(getApplicationContext(), "Device's orientation monitoring stopped...", Toast.LENGTH_LONG).show();
			}			
			break;
		case R.id.checkSync:
			if(checked)
			{
				interfaceManager.setmSync(true);
				startService(interfaceManager.getIntent());
				Toast.makeText(getApplicationContext(), "Browsed files under internet connection will be sent.", Toast.LENGTH_LONG).show();
			}
			else
			{
				interfaceManager.setmSync(false);
				stopService(interfaceManager.getIntent());
				Toast.makeText(getApplicationContext(), "Browsed files under intenrnet connection won't be sent.", Toast.LENGTH_LONG).show();
			}
		}
		}
	
	private boolean connected( )
    {
		boolean connected = false;
    	ConnectivityManager cm =
    	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
    	        connected = true;
    	    }
    	    System.out.println(connected);
        return connected;
    }
		
	}