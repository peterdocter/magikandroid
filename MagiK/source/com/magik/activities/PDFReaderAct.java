package com.magik.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.magik.R;
import com.magik.db.PersistenceManager;
import com.magik.mundo.controllers.RotationControlService;
import com.magik.mundo.data.ControlerData;
import com.magik.mundo.monitoring.ControlerDisplay;
import com.magik.mundo.monitoring.DatosDisplay;
import com.magik.mundo.pdf.PDFReader;
import com.magik.mundo.pdf.PDFReader.PDFReaderListener;
import com.magik.services.WebServiceConnection;
import com.magik.services.tasks.SendPDFTask;
import com.radaee.grid.PDFGridItem;
import com.radaee.grid.PDFGridView;
import com.radaee.pdf.Document;
import com.radaee.pdf.Global;
import com.radaee.reader.PDFThumbView;
import com.radaee.view.PDFVPage;
import com.radaee.view.PDFViewThumb.PDFThumbListener;

public class PDFReaderAct extends Activity implements OnItemClickListener, OnClickListener, PDFReaderListener, PDFThumbListener
{
    private PDFGridView m_vFiles = null;
    private PDFReader m_reader = null;
    private PDFThumbView m_thumb = null;
    private RelativeLayout m_layout;
    private Document m_doc = new Document( );
    // private Button btn_close;
    private Button btn_rec;

    private boolean m_set = false;
    private ArrayList<String> recs;
    private String[] recsPDF;
    private ArrayList<String> palabras;

    private Thread thread;
    private Thread recommendationsThread;

    private String CLASE_DISPLAY = "";
    private ControlerData data;
    private DatosDisplay datosDisplay;

    private boolean sensorProcess;
    private PersistenceManager persistenceManager;
    private String path;
    
    private InterfaceManager manager;
    private RotationControlService rotationService;
    
    private ArrayList<String> swipes;

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        path = "";
        data = new ControlerData( );
        palabras = new ArrayList<String>( );
        recs = new ArrayList<String>( );
        sensorProcess = true;
        manager = InterfaceManager.getInstance( );
        datosDisplay = DatosDisplay.darInstacia( );
        if(manager.ismGyro())
        {
        	Intent service = new Intent(getApplicationContext(), RotationControlService.class);
            startService(service);
            service = null;
        }
        
        recommendationsThread = new Thread( )
        {
            @Override
            public void run( )
            {
            	swipes = datosDisplay.getSwipes();
                if(manager.ismGyro())
                {
                	rotationService = RotationControlService.getInstance();
                	while( sensorProcess && manager.ismAct( ) && rotationService.isReading())
                    {
                		Log.v( "KELVIN", "swipes: " + swipes.size() + " " + swipes.toString() );
                        try
                        {
                            if( swipes.size( ) > 1 )
                            {
                                int cardinalidadLectura = cardinalidadLecturas( );
                                int cardinalidadBusqueda = cardinalidadBusqueda( );
                                int cardinalidadLecturarapida = cardinalidadLecturaRapida( );
                                double analisis = (cardinalidadLectura + (cardinalidadLecturarapida/2))/(cardinalidadBusqueda + cardinalidadLectura + cardinalidadLecturarapida);
                                if(analisis>0.5)
                                {
                                	Log.d( "Lecturas", String.valueOf( datosDisplay.getLecturas( ).size( ) ) );
                                	Log.d( "Recomendaciones", "Hizo el thread" );
                                	recomendacionesPDF( );
                                	sensorProcess = false;                            	
                                }
                            }
                        	sleep( 10000 );
                        }
                        catch( InterruptedException e )
                        {
                            e.printStackTrace( );
                        }
                    }
                }
                else
                {
                	while( sensorProcess && manager.ismAct( ))
                    {
                        try
                        {
                            Log.v( "KELVIN", "Save Reads" );
                            Log.v( "KELVIN", "swipes: " + swipes.size() + " " + swipes.toString() );
                            if( swipes.size( ) > 1 )
                            {
                                int cardinalidadLectura = cardinalidadLecturas( );
                                int cardinalidadBusqueda = cardinalidadBusqueda( );
                                int cardinalidadLecturarapida = cardinalidadLecturaRapida( );
                                double analisis = (cardinalidadLectura + (cardinalidadLecturarapida/2))/(cardinalidadBusqueda + cardinalidadLectura + cardinalidadLecturarapida);
                                if(analisis>0.5)
                                {                                	
                                	Log.d( "Lecturas", String.valueOf( datosDisplay.getLecturas( ).size( ) ) );
                                	Log.d( "Recomendaciones", "Hizo el thread" );
                                	recomendacionesPDF( );
                                	sensorProcess = false;                                
                                }
                            }
                            sleep( 10000 );
                        }
                        catch( InterruptedException e )
                        {
                            e.printStackTrace( );
                        }
                    }
                }
                
            }
        };
        recommendationsThread.start( );

        Global.Init( this );
        m_vFiles = new PDFGridView( this, null );
        m_vFiles.PDFSetRootPath( "/mnt" );
        m_vFiles.setOnItemClickListener( this );
        setContentView( m_vFiles );
        m_layout = ( RelativeLayout )LayoutInflater.from( this ).inflate( R.layout.reader, null );
        m_reader = ( PDFReader )m_layout.findViewById( R.id.view );
        m_thumb = ( PDFThumbView )m_layout.findViewById( R.id.thumbs );
        LinearLayout bar_cmd = ( LinearLayout )m_layout.findViewById( R.id.bar_cmd );
        // LinearLayout bar_act = ( LinearLayout )m_layout.findViewById( R.id.bar_act );
        // LinearLayout bar_find = ( LinearLayout )m_layout.findViewById( R.id.bar_find );
        // btn_close = ( Button )bar_cmd.findViewById( R.id.btn_close );
        btn_rec = ( Button )bar_cmd.findViewById( R.id.btnRec );
        btn_rec.setOnClickListener( this );

    }

    public void guardar( )
    {
        DatosDisplay datos = DatosDisplay.darInstacia( );
        ArrayList<String> respuestax = datos.getAnalisiX( );
        ArrayList<String> respuestay = datos.getAnalisiY( );

        String rtax = "";
        String rtay = "";
        for( int i = 0; i < respuestax.size( ); i++ )
        {
            rtax = rtax + respuestax.get( i ) + "\n";
        }
        for( int i = 0; i < respuestay.size( ); i++ )
        {
            rtay = rtay + respuestay.get( i ) + "\n";
        }
        String rta = rtax + "\n" + rtay;
        
        if( respuestax.size( ) > 0 )
        {
            data.writeToFile( rta, true, CLASE_DISPLAY );
            data.writeToFile( rta, true, CLASE_DISPLAY );
        }
        rta = "";
    }

    @Override
	protected void onDestroy( )
    {
        // m_vFiles.close();
        if( m_vFiles != null )
        {
            m_vFiles.close( );
            m_vFiles = null;
        }
        if( m_thumb != null )
        {
            m_thumb.thumbClose( );
            m_thumb = null;
        }
        if( m_reader != null )
            m_reader.PDFClose( );
        if( m_doc != null )
            m_doc.Close( );
        Global.RemoveTmp( );
        stopService(new Intent(getApplicationContext(), RotationControlService.class));
        super.onDestroy( );
    }
    
    @Override
    protected void onPause() {
    	stopService(new Intent(getApplicationContext(), RotationControlService.class));
    	super.onPause();
    }

    @Override
	public void onItemClick( AdapterView<?> arg0, View arg1, int arg2, long arg3 )
    {
        if( arg0 == m_vFiles )
        {
            PDFGridItem item = ( PDFGridItem )arg1;

            if( item.is_dir( ) )
            {
                path += item.get_name( ) + "/";
                m_vFiles.PDFGotoSubdir( item.get_name( ) );
            }
            else
            {
                m_doc.Close( );
                path += item.get_name( );

                String datos[] = item.get_name( ).split( "/" );
                CLASE_DISPLAY = CLASE_DISPLAY + datos[ datos.length - 1 ];
                int ret = item.open_doc( m_doc, null );
                switch( ret )
                {
                    case -1:// need input password
                        finish( );
                        break;
                    case -2:// unknown encryption
                        finish( );
                        break;
                    case -3:// damaged or invalid format
                        finish( );
                        break;
                    case -10:// access denied or invalid file path
                        finish( );
                        break;
                    case 0:// succeeded, and continue
                        m_reader.PDFOpen( m_doc, this );
                        break;
                    default:// unknown error
                        finish( );
                        break;
                }
                m_thumb.thumbOpen( m_reader.PDFGetDoc( ), this );
                setContentView( m_layout );

                data.existDelete( CLASE_DISPLAY );
                data.crearFile( CLASE_DISPLAY, "Acci�n;Direcci�n;Velocidad (pixeles/seg);Eje" );
                sensorProcess = true;
                thread = new Thread( )
                {
                    @Override
                    public void run( )
                    {
                        while( sensorProcess )
                        {
                            try
                            {
                                Log.v( "KELVIN", "EEEENNNTROOO" );
                                guardar( );
                                sleep( 12000 );
                            }
                            catch( InterruptedException e )
                            {
                                e.printStackTrace( );
                            }
                        }
                    }
                };
                thread.start( );

            }
        }
        else
        {
        }
    }

    private void onSelect( )
    {
        m_set = !m_set;
        m_reader.PDFSetSelect( );
    }

    @Override
	public void onClick( View v )
    {
        switch( v.getId( ) )
        {
            case R.id.btn_close:
                m_thumb.thumbClose( );
                m_reader.PDFClose( );
                if( m_doc != null )
                    m_doc.Close( );
                setContentView( m_vFiles );
                break;
            case R.id.btnRec:
                actualizarTextoLecturas( recs );
                AlertDialog.Builder builder = new AlertDialog.Builder( PDFReaderAct.this );
                builder.setTitle( "Recomendaciones" );
                if( recsPDF != null && recsPDF.length > 0 )
                {
                    builder.setMessage( Arrays.deepToString( recsPDF ) );
                }
                else
                {
                    builder.setMessage( "No hay recomendaciones disponibles." );
                }
                builder.setCancelable( true );
                AlertDialog alert = builder.create( );
                alert.show( );
                break;

        }
    }

    @Override
	public void OnPageClicked( int pageno )
    {
        m_reader.PDFGotoPage( pageno );
    }

    @Override
	public void OnPageChanged( int pageno )
    {
        m_thumb.thumbGotoPage( pageno );
    }

    @Override
	public void OnAnnotClicked( PDFVPage vpage, int annot )
    {
    }

    private void palabrasClave( )
    {
        File f = new File( path );
        SendPDFTask task = new SendPDFTask( );
        try
        {
            Object[] params = new Object[2];
            params[ 0 ] = f;
            params[ 1 ] = WebServiceConnection.PALABRAS_FILE;
            task.execute( params );
            String resp = task.get( );
            System.out.println( "Respuesta: " + resp );
            if( resp.contains( ";" ) )
            {
                String[] recArr = resp.split( ";" );
                for( String rec : recArr )
                {
                    palabras.add( rec );
                }
            }
            else
            {
                palabras.add( resp );
            }
            params = null;
        }
        catch( Exception e )
        {
            System.out.println( e.getMessage( ) );
        }
        f = null;
        task = null;
        System.gc( );
    }

    private void recomendaciones( )
    {
        File f = new File( path );
        SendPDFTask task = new SendPDFTask( );
        try
        {
            Object[] params = new Object[2];
            params[ 0 ] = f;
            params[ 1 ] = WebServiceConnection.RECOMEND_FILE;
            task.execute( params );
            String resp = task.get( );
            System.out.println( "Respuesta: " + resp );
            if( resp.contains( ";" ) )
            {
                String[] recArr = resp.split( ";" );
                for( String rec : recArr )
                {
                    recs.add( rec );
                }
                persistenceManager = new PersistenceManager( this.getApplicationContext( ) );
                persistenceManager.saveRecommendations( CLASE_DISPLAY, recArr );
                persistenceManager = null;
            }
            else
            {
                recs.add( resp );
            }
            params = null;
        }
        catch( Exception e )
        {
            System.out.println( e.getMessage( ) );
        }
        System.gc( );
        f = null;
        task = null;
        System.gc( );
    }

    private boolean connected( )
    {
    	boolean connected = false;
    	ConnectivityManager cm =
    	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
    	        connected = true;
    	    }
    	    System.out.println(connected);
        return connected;
    }

    protected void recomendacionesPDF( )
    {
        try
        {
        	persistenceManager = new PersistenceManager( getApplicationContext( ) );
        	String pathTemp = path;
            if( connected( ) )
            {
                recomendaciones( );
                palabrasClave( );
                persistenceManager.setSynced(pathTemp);
            }
            String[] pals = new String[palabras.size( )];
            pals = palabras.toArray( pals );
            // TODO quitar este arreglo
            pals = new String[] { "Kelvin", "Tita", "Julio" };
            if( pals.length > 0 )
            {
                if( !persistenceManager.isFileInTable( pathTemp ) )
                {
                    persistenceManager.createDocument( pathTemp, PersistenceManager.PDF, CLASE_DISPLAY );

                }
                sensorProcess = false;
                ArrayList<String> guardar = new ArrayList<String>( );
                for( String palabra : pals )
                {
                    if( !persistenceManager.isPKInTable( palabra, pathTemp ) )
                    {
                        guardar.add( palabra );
                    }
                }
                String[] g = new String[guardar.size( )];
                g = guardar.toArray( g );
                guardar = null;
                persistenceManager.savePalabrasClave( pathTemp, g );
                g = null;
                ArrayList<String> recomms = persistenceManager.recomendar( pathTemp );
                recsPDF = new String[recomms.size( )];
                for( int i = 0; i < recsPDF.length; i++ )
                {
                    recsPDF[ i ] = recomms.get( i );
                }
                persistenceManager.saveRecommendations( pathTemp, recsPDF );
                System.out.println( Arrays.deepToString( recsPDF ) );
                Vibrator vibrator = ( Vibrator )PDFReaderAct.this.getSystemService( VIBRATOR_SERVICE );
                vibrator.vibrate( 3000 );

                pals = null;
                recomms = null;
                persistenceManager = null;
                for( String r : recsPDF )
                {
                    recs.add( r );
                }
            }

        }
        catch( Exception e )
        {
            System.err.println( e.getMessage( ) );
        }
        System.gc( );
    }

    @Override
	public void OnOpenURI( String uri )
    {
    }

    @Override
	public void OnOpenMovie( String path )
    {
    }

    @Override
	public void OnOpenSound( int[] paras, String path )
    {
    }

    @Override
	public void OnOpenAttachment( String path )
    {
    }

    @Override
	public void OnOpen3D( String path )
    {
    }

    @Override
	public void OnSelectEnd( String text )
    {
        LinearLayout layout = ( LinearLayout )LayoutInflater.from( this ).inflate( R.layout.dlg_text, null );
        final RadioGroup rad_group = ( RadioGroup )layout.findViewById( R.id.rad_group );
        final String sel_text = text;

        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setPositiveButton( "OK", new DialogInterface.OnClickListener( )
        {
            @Override
			public void onClick( DialogInterface dialog, int which )
            {
                if( rad_group.getCheckedRadioButtonId( ) == R.id.rad_mWeb )
                    Toast.makeText( PDFReaderAct.this, "todo copy text:" + sel_text, Toast.LENGTH_SHORT ).show( );
                else if( m_reader.PDFCanSave( ) )
                {
                    boolean ret = false;
                    if( rad_group.getCheckedRadioButtonId( ) == R.id.rad_mFiles )
                        ret = m_reader.PDFSetSelMarkup( 0 );
                    else if( rad_group.getCheckedRadioButtonId( ) == R.id.rad_mAct )
                        ret = m_reader.PDFSetSelMarkup( 1 );
                    else if( rad_group.getCheckedRadioButtonId( ) == R.id.rad_strikeout )
                        ret = m_reader.PDFSetSelMarkup( 2 );
                    if( !ret )
                        Toast.makeText( PDFReaderAct.this, "add annotation failed!", Toast.LENGTH_SHORT ).show( );
                }
                else
                    Toast.makeText( PDFReaderAct.this, "can't write or encrypted!", Toast.LENGTH_SHORT ).show( );
                onSelect( );
                dialog.dismiss( );
            }
        } );
        builder.setNegativeButton( "Cancel", new DialogInterface.OnClickListener( )
        {
            @Override
			public void onClick( DialogInterface dialog, int which )
            {
                dialog.dismiss( );
            }
        } );
        builder.setTitle( "Process selected text" );
        builder.setCancelable( false );
        builder.setView( layout );
        AlertDialog dlg = builder.create( );
        dlg.show( );
    }

    public void actualizarTextoLecturas( ArrayList<String> texto )
    {
        TextView text = ( TextView )findViewById( R.id.txtVerb );
        if( texto != null )
        {
            for( int i = 0; i < texto.size( ); i++ )
            {
                text.clearComposingText( );
                text.setText( texto.get( i ) );
                System.out.println( texto.get( i ) );
            }
        }
        else
        {
            text.clearComposingText( );
            text.setText( "Cargando..." );
        }
    }
    
    public int cardinalidadLecturaRapida( )
    {
        int cardinalidad = 0;
        for( String swipe : swipes )
        {
            if( swipe.equals( ControlerDisplay.LECTURA_RAPIDA ) )
            {
                cardinalidad++;
            }
        }
        return ( int ) ( cardinalidad * 0.5 );
    }

    public int cardinalidadLecturas( )
    {
        int cardinalidad = 0;
        for( String swipe : swipes )
        {
            if( swipe.equals( ControlerDisplay.LECTURA ) )
            {
                cardinalidad++;
            }
        }
        return cardinalidad;
    }

    public int cardinalidadBusqueda( )
    {
        int cardinalidad = 0;
        for( String swipe : swipes )
        {
            if( swipe.equals( ControlerDisplay.BUSQUEDA ) )
            {
                cardinalidad++;
            }
        }
        return ( int ) ( cardinalidad * 0.2 );
    }

}
