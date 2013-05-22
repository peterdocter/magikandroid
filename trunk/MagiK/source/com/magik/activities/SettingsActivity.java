/**
 * 
 */
package com.magik.activities;

import com.magik.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

/**
 * This class is the input processing of the settings user interface.
 * @author User
 *
 */
public class SettingsActivity extends Activity {
	
	private RadioButton rad_mWeb;
	
	private RadioButton rad_mFiles;
	
	private RadioButton rad_mAct;
	
	private InterfaceManager interfaceManager;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		rad_mWeb = (RadioButton)findViewById(R.id.rad_mWeb);
		rad_mFiles = (RadioButton)findViewById(R.id.rad_mFiles);
		rad_mAct = (RadioButton)findViewById(R.id.rad_mAct);
		interfaceManager = InterfaceManager.getInstance();
	}

	public void onRadioButtonClicked(View view)
	{
		boolean checked = ((RadioButton)view).isChecked();
		
		switch (view.getId()) {
		case R.id.rad_mWeb:
			if(checked)
			{
				interfaceManager.setmWeb(true);
			}
			else
			{
				interfaceManager.setmWeb(false);
			}
			Toast.makeText( this, interfaceManager.ismWeb( )+"", Toast.LENGTH_LONG ).show();
			break;
		case R.id.rad_mFiles:
			if(checked)
			{
				interfaceManager.setmFiles(true);
			}
			else
			{
				interfaceManager.setmFiles(false);
			}
			Toast.makeText( this, interfaceManager.ismFiles( )+"", Toast.LENGTH_LONG ).show();
			break;
		case R.id.rad_mAct:
			if(checked)
			{
				interfaceManager.setmAct(true);
			}
			else
			{
				interfaceManager.setmAct(false);
			}
			Toast.makeText( this, interfaceManager.ismAct( )+"", Toast.LENGTH_LONG ).show();
			break;
		}
	}
	

}
