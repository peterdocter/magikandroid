package com.contolers.magik.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.contolers.magik.data.ControlerData;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class ProcessMonitor extends Service
{
    private String CLASE_FILE = "prueba";
    private ControlerData data;
    private File processFile;
    private ActivityManager am;
    private PackageManager pm;
    private Thread thread;

    @Override
    public void onCreate( )
    {
        
        super.onCreate( );
        // File initializer
        File root = android.os.Environment.getExternalStorageDirectory( );
        Log.i( "STORAGE", "External file system root: " + root );
        data = new ControlerData();
        data.crearFile(CLASE_FILE, "TIME;Process;Foreground;Perceptible;Visible");
        
        // Process initializer
        am = ( ActivityManager )this.getSystemService( ACTIVITY_SERVICE );
        pm = this.getPackageManager( );
        thread = new Thread( )
        {
            @Override
            public void run( )
            {
                try
                {
                        List l = am.getRunningAppProcesses( );
                        Iterator i = l.iterator( );
                        while( i.hasNext( ) )
                        {
                            ActivityManager.RunningAppProcessInfo info = ( ActivityManager.RunningAppProcessInfo ) ( i.next( ) );
                            try
                            {
                                Log.i( "PRUEBA", "Estoy pasando por acá" );
                                ApplicationInfo apInfo = pm.getApplicationInfo( info.processName, PackageManager.GET_META_DATA );
                                boolean inForeground = info.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
                                boolean isPerceptible = info.importance == RunningAppProcessInfo.IMPORTANCE_PERCEPTIBLE;
                                boolean isVisible = info.importance == RunningAppProcessInfo.IMPORTANCE_VISIBLE;
                                CharSequence labelProcess = pm.getApplicationLabel( apInfo );
                                data.writeToFile( labelProcess.toString( ) + ";" + inForeground + ";" + isPerceptible + ";" + isVisible, true, CLASE_FILE );
                            }
                            catch( Exception e )
                            {
                                e.printStackTrace( );
                            }
                        }
                        sleep( 120000 );//2 minutos - 5 minutos es 300000
                }
                catch( InterruptedException e )
                {
                    e.printStackTrace( );
                }
            }
        };
        thread.start( );

    }
    
    public String getDayFileName( )
    {
        Calendar c = Calendar.getInstance( );
        int month = c.get( Calendar.MONTH ) + 1;
        int day = c.get( Calendar.DAY_OF_MONTH );
        int year = c.get( Calendar.YEAR );
        return day + "-" + month + "-" + year;
    }

    public String getTimeStamp( )
    {
        Calendar c = Calendar.getInstance( );
        int seconds = c.get( Calendar.SECOND );
        int minutes = c.get( Calendar.MINUTE );
        int hour = c.get( Calendar.HOUR_OF_DAY );
        return hour + ":" + minutes + "." + seconds;
    }

    public void writeToFile( String text, boolean time )
    {
        try
        {
            FileOutputStream f = new FileOutputStream( processFile, true );
            PrintWriter pw = new PrintWriter( f );
            if( time )
            {
                pw.append( getTimeStamp( ) + ";" );
            }
            pw.append( text );
            pw.append( "\n" );
            pw.flush( );
            pw.close( );
            f.close( );
        }
        catch( FileNotFoundException e )
        {
            e.printStackTrace( );
            Log.i( "PROBLEMA", "******* File not found. Did you" + " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?" );
        }
        catch( IOException e )
        {
            e.printStackTrace( );
        }
    }
    

    @Override
    public IBinder onBind( Intent intent )
    {
        Bundle extras = intent.getExtras();
        if(extras == null)
            Log.d("Service","null");
        else
        {
            Log.d("Service","not null");
            CLASE_FILE = (String) extras.get("From");

        }
        return null;
    }

    @Override
    public void onDestroy( )
    {
        super.onDestroy( );
        thread.interrupt( );
    }
}
