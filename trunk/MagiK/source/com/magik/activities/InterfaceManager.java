/**
 * 
 */
package com.magik.activities;

import android.content.Context;
import android.content.Intent;

import com.magik.mundo.controllers.FileModificationService;
import com.magik.mundo.controllers.MonitoreoWebService;

/**
 * @author User
 * 
 */
public class InterfaceManager
{

    private static InterfaceManager instance;

    private boolean mWeb;

    private boolean mFiles;

    private boolean mAct;
    
    private boolean mGyro;

    private Context context;

    private Intent intent;

    public InterfaceManager( )
    {
        mWeb = false;

        mFiles = false;

        mAct = false;
        
        mGyro = false;
    }

    public Context getContext( )
    {
        return context;
    }

    public void setContext( Context context )
    {
        this.context = context;
    }

    public static InterfaceManager getInstance( )
    {
        if( instance == null )
        {
            instance = new InterfaceManager( );
        }
        return instance;
    }

    /**
     * @return the mWeb
     */
    public boolean ismWeb( )
    {
        return mWeb;
    }

    /**
     * @param mWeb the mWeb to set
     */
    public void setmWeb( boolean mWeb )
    {
        this.intent = new Intent( context, MonitoreoWebService.class );
        this.mWeb = mWeb;
    }

    /**
     * @return the mFiles
     */
    public boolean ismFiles( )
    {
        return mFiles;
    }

    /**
     * @param mFiles the mFiles to set
     */
    public void setmFiles( boolean mFiles )
    {
        this.intent = new Intent( context, FileModificationService.class );
        this.mFiles = mFiles;
    }

    /**
     * @return the mAct
     */
    public boolean ismAct( )
    {
        return mAct;
    }

    /**
     * @param mAct the mAct to set
     */
    public void setmAct( boolean mAct )
    {
        this.mAct = mAct;
    }    

    /**
	 * @return the mGyro
	 */
	public boolean ismGyro() {
		return mGyro;
	}

	/**
	 * @param mGyro the mGyro to set
	 */
	public void setmGyro(boolean mGyro) {
		this.mGyro = mGyro;
	}

	public Intent getIntent( )
    {
        return intent;
    }

}
