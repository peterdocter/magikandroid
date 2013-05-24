package com.magik.mundo.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;

import android.util.Log;

public class ControlerData
{
    private Hashtable<String, File> files;

    public ControlerData( )
    {
        files = new Hashtable<String, File>( );
    }

    public void crearFile( String pMonitor, String encabezado )
    {
        // File initializer
        File root = android.os.Environment.getExternalStorageDirectory( );
        Log.i( "STORAGE", "External file system root: " + root );
        File dir = new File( root.getAbsolutePath( ) + "/Magik/" + getDayFileName( ) );
        dir.mkdirs( );
        File monitorFile = new File( dir, pMonitor + ".txt" );
        files.put( pMonitor, monitorFile );
        writeToFile( encabezado, false, pMonitor );
        
        root = null;
        dir= null;
        monitorFile = null;
        System.gc( );
    }

    public String getDayFileName( )
    {
        Calendar c = Calendar.getInstance( );
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        
        return formattedDate;
    }

    public String getTimeStamp( )
    {
        Calendar c = Calendar.getInstance( );
        int seconds = c.get( Calendar.SECOND );
        int minutes = c.get( Calendar.MINUTE );
        int hour = c.get( Calendar.HOUR_OF_DAY );
        return hour + ":" + minutes + "." + seconds;
    }

    public void existDelete(String pMonitor) {
		File temp = files.get(pMonitor);
		if(temp != null)
		{
    		if ( temp.exists() ) 
    		{
    			temp.delete();
    		}
		}
		temp = null;
		System.gc( );
	}
    
    public void writeToFile( String text, boolean time, String pMonitor )
    {
        try
        {
            File temp = files.get( pMonitor );
            FileOutputStream f = new FileOutputStream( temp, true );
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
            
            temp = null;
            f = null;
            pw = null;
            
            System.gc( );
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
}
