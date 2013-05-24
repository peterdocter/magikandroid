/**
 * 
 */
package com.magik.mundo.controllers;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author Kelvin
 *
 */
public class RotationControlService extends Service
{
    
    /* (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind( Intent arg0 )
    {
        
        return null;
    }

}
