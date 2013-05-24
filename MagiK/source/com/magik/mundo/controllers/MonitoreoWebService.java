package com.magik.mundo.controllers;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
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
            if( !pm.isFileInTable( documentName ) )
            {
                pm.createDocument( documentName, documentType, documentTitle );
            }
        }
        catch( Exception e )
        {
            e.printStackTrace( );
        }
    }
    
    private Cursor cargarDatosWeb( )
    {
        String[] selection = { Browser.BookmarkColumns._ID, Browser.BookmarkColumns.URL, Browser.BookmarkColumns.TITLE };
        Cursor cursor = getContentResolver( ).query( Browser.BOOKMARKS_URI, selection, null, null, Browser.BookmarkColumns.DATE + " DESC LIMIT 30" );
        guardarDatos( cursor );
        return cursor;
    }

}
