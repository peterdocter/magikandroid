/**
 * 
 */
package com.magik.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

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
		interfaceManager = InterfaceManager.getInstance();
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
				
			}
			else
			{
				interfaceManager.setmAct(false);				
			}			
			break;
		}
	}
	

}
