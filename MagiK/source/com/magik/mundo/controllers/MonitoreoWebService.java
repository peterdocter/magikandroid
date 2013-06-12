package com.magik.mundo.controllers;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.provider.Browser;

import com.magik.db.PersistenceManager;
import com.magik.mundo.data.ControlerData;

public class MonitoreoWebService extends Service
{
    private static String CLASE_MONITOREO = "MWEB";
    private ControlerData artData;
    private PersistenceManager pm;
    private Thread t;
    private boolean sensorProcess;

    @Override
    public void onCreate( )
    {
        artData = new ControlerData( );
    }

    @Override
    public void onStart( Intent intent, int startid )
    {
        artData = new ControlerData( );
        artData.crearFile( CLASE_MONITOREO, "TIME;Numero de consulta;Titulo;Url" );
        sensorProcess = true;
        t = new Thread( )
        {
            @Override
            public void run( )
            {
                while( sensorProcess )
                {
                    try
                    {
                        cargarDatosWeb( );
                        sleep( 30000 );
                    }
                    catch( InterruptedException e )
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace( );
                    }
                }
            }
        };
        t.start( );
    }

    @Override
    public void onDestroy( )
    {
        sensorProcess = false;
    }

    @Override
    public IBinder onBind( Intent arg0 )
    {
        return null;
    }

    
    private void guardarDatos( Cursor cursor )
    {
        artData.existDelete( CLASE_MONITOREO );
        cursor.moveToFirst( );
        while( cursor.isAfterLast( ) == false )
        {
            String vrTitle = cursor.getString( 2 );
            String vrUrl = cursor.getString( 1 );
            if( vrTitle == null )
                vrTitle = "";
            if( vrUrl == null )
                vrUrl = "";
            artData.writeToFile( vrTitle + "," + vrUrl + "\n", true, CLASE_MONITOREO );
            guardarDocumentoBase( vrUrl, vrTitle, PersistenceManager.HTML );
            cursor.moveToNext( );
        }
    }

    private void guardarDocumentoBase( String documentName, String documentTitle, String documentType )
    {
        try
        {
        	pm = new PersistenceManager(getApplicationContext());
            if( !pm.isFileInTable( documentName ) )
            {
                pm.createDocument( documentName, documentType, documentTitle );
            }
        }
        catch( Exception e )
        {
        }
    }
    
    private Cursor cargarDatosWeb( )
    {
        String[] projection = { BaseColumns._ID, Browser.BookmarkColumns.URL, Browser.BookmarkColumns.TITLE };
        String selection = Browser.BookmarkColumns.BOOKMARK + "= 0";
        ContentResolver resolver = getContentResolver();
        Cursor cursor = null;
        try {
        	cursor = resolver.query( Browser.BOOKMARKS_URI, projection, selection, null, Browser.BookmarkColumns.DATE + " DESC LIMIT 30" );
        	guardarDatos( cursor );
		} catch (Exception e) {
			this.stopSelf();
		}        
        return cursor;
    }

}
