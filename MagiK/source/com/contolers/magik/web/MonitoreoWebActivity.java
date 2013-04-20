package com.contolers.magik.web;

import net.sf.andpdf.pdfviewer.R;
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

import com.contolers.magik.data.ControlerData;

public class MonitoreoWebActivity extends Activity
{
	private static String CLASE_MONITOREO = "MWEB";
    private ListView atrListView;
    private Button btnStart;
    private Button btnClear;
    private static boolean started = false;
    private SimpleCursorAdapter adapter;
    private ControlerData artData;

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_monitoreo_web );
        artData = new ControlerData();
        artData.crearFile(CLASE_MONITOREO, "TIME;Numero de consulta;Titulo;Url");
        btnStart = ( Button )findViewById( R.id.web_control_start );
        btnClear = ( Button )findViewById( R.id.web_control_clears );
        atrListView = ( ListView )findViewById( R.id.ListWeb_List );
        inicialiarList( cargarDatosWeb() );
        inicializarBotones();
    }

    private void inicializarBotones()
    {
        int screenWidth = this.getWindowManager( ).getDefaultDisplay( ).getWidth( );
        if( !started )
        {
            btnStart.setEnabled( false );
            cargarDatosLista(cargarDatosWeb( ));
            started = true;
        }
        btnStart.setOnClickListener( new View.OnClickListener( )
        {
            // @Override
            public void onClick( View v )
            {
                cargarDatosLista(cargarDatosWeb( ));
                btnStart.setEnabled( false );
                started = true;
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
        cargarDatosLista(cargarDatosWeb( ));
        adapter.notifyDataSetChanged( );
    }

    public void clearLog( )
    {
        String[] values = new String[] { "" };
        String[] displayFields = { Browser.BookmarkColumns.URL, Browser.BookmarkColumns.TITLE };
        int[] viewFields = { R.id.textlistNombre, R.id.textlistDescription };
        ArrayAdapter<String> adapterVacio = new ArrayAdapter<String>(this,R.layout.rowlist_monitoreo_web, R.id.textlistNombre, values);
        atrListView.setAdapter( adapterVacio );
        adapter = null;
        
    }
    
    private void cargarDatosLista(Cursor cursor)
    {
        
    	String[] displayFields = { Browser.BookmarkColumns.URL, Browser.BookmarkColumns.TITLE };
        int[] viewFields = { R.id.textlistNombre, R.id.textlistDescription };

        adapter = new SimpleCursorAdapter( this, R.layout.rowlist_monitoreo_web, cursor, displayFields, viewFields );
        atrListView.setAdapter( adapter );
        
    }
    
    private void guardarDatos(Cursor cursor)
    {
    	artData.existDelete(CLASE_MONITOREO);
    	String rta ="";
    	cursor.moveToFirst();
    	while (cursor.isAfterLast() == false) 
    	{
    		String string0=cursor.getString(0);
    		String string1=cursor.getString(1);
    		
    		String string2=cursor.getString(2);
    		if(string0==null)
    			string0="";
    		if(string1==null)
    			string1="";
    		if(string2==null)
    			string2="";
    		artData.writeToFile( string0 + ","  + string2 + "," + string1 +"\n" , true, CLASE_MONITOREO);
    	    cursor.moveToNext();
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
        cargarDatosLista(cursor);
    }
    
    private Cursor cargarDatosWeb()
    {
        String[] selection = { Browser.BookmarkColumns._ID, Browser.BookmarkColumns.URL, Browser.BookmarkColumns.TITLE };
        Cursor cursor = getContentResolver( ).query( Browser.BOOKMARKS_URI, selection, null, null,  Browser.BookmarkColumns.DATE + " DESC" );
        startManagingCursor( cursor );
        guardarDatos( cursor );
        return cursor;

    }

    
}
