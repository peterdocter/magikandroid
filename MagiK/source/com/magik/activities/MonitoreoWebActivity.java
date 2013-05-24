package com.magik.activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Browser;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.magik.R;
import com.magik.db.PersistenceManager;
import com.magik.mundo.data.ControlerData;

public class MonitoreoWebActivity extends Activity
{
    private static String CLASE_MONITOREO = "MWEB";
    private ListView atrListView;
    private Button btnStart;
    private Button btnClear;
    private static boolean started = false;
    private SimpleCursorAdapter adapter;
    private ControlerData artData;
    private PersistenceManager pm;
    private Thread t;
    private boolean sensorProcess;

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        pm = new PersistenceManager( this.getApplicationContext( ) );
        setContentView( R.layout.activity_monitoreo_web );
        artData = new ControlerData( );
        artData.crearFile( CLASE_MONITOREO, "TIME;Numero de consulta;Titulo;Url" );
        btnStart = ( Button )findViewById( R.id.web_control_start );
        btnClear = ( Button )findViewById( R.id.web_control_clears );
        atrListView = ( ListView )findViewById( R.id.ListWeb_List );
        inicialiarList( cargarDatosWeb( ) );
        inicializarBotones( );
    }

    private void inicializarBotones( )
    {
        int screenWidth = this.getWindowManager( ).getDefaultDisplay( ).getWidth( );
        if( !started )
        {
            btnStart.setEnabled( false );
            cargarDatosLista( cargarDatosWeb( ) );
            started = true;
        }
        btnStart.setOnClickListener( new View.OnClickListener( )
        {
            // @Override
            public void onClick( View v )
            {
                final InterfaceManager manager = InterfaceManager.getInstance( );
                t = new Thread( )
                {
                    @Override
                    public void run( )
                    {
                        while( sensorProcess && manager.ismWeb( ))
                        {
                            try
                            {
                                cargarDatosLista( cargarDatosWeb( ) );
                                btnStart.setEnabled( false );
                                started = true;
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
        } );

        btnClear.setWidth( screenWidth / 4 );
        btnClear.setOnClickListener( new View.OnClickListener( )
        {
            // @Override
            public void onClick( View v )
            {
                clearLog( );
                started = false;
                btnStart.setEnabled( true );
            }
        } );
        refreshLog( );
    }

    public void refreshLog( )
    {
        cargarDatosLista( cargarDatosWeb( ) );
        adapter.notifyDataSetChanged( );
    }

    public void clearLog( )
    {
        String[] values = new String[]{ "" };
        ArrayAdapter<String> adapterVacio = new ArrayAdapter<String>( this, R.layout.rowlist_monitoreo_web, R.id.textlistNombre, values );
        atrListView.setAdapter( adapterVacio );
        adapter = null;

    }

    private void cargarDatosLista( Cursor cursor )
    {

        String[] displayFields = { Browser.BookmarkColumns.URL, Browser.BookmarkColumns.TITLE };
        int[] viewFields = { R.id.textlistNombre, R.id.textlistDescription };
        adapter = new SimpleCursorAdapter( this, R.layout.rowlist_monitoreo_web, cursor, displayFields, viewFields );
        atrListView.setAdapter( adapter );

    }

    private void guardarDatos( Cursor cursor )
    {
        artData.existDelete( CLASE_MONITOREO );
        String rta = "";
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

    private void inicialiarList( Cursor cursor )
    {
        atrListView.setOnItemClickListener( new OnItemClickListener( )
        {
            public void onItemClick( AdapterView<?> parent, View view, int position, long id )
            {
                TextView view2 = ( TextView )view.findViewById( R.id.textlistDescription );
                Toast.makeText( getApplicationContext( ), view2.getText( ), Toast.LENGTH_SHORT ).show( );
            }
        } );
        cargarDatosLista( cursor );
    }

    private Cursor cargarDatosWeb( )
    {
        String[] selection = { Browser.BookmarkColumns._ID, Browser.BookmarkColumns.URL, Browser.BookmarkColumns.TITLE };
        Cursor cursor = getContentResolver( ).query( Browser.BOOKMARKS_URI, selection, null, null, Browser.BookmarkColumns.DATE + " DESC LIMIT 30" );
        guardarDatos( cursor );
        return cursor;
    }

}
