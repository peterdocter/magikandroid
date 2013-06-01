package com.magik.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.magik.R;
import com.magik.db.PersistenceManager;
import com.magik.mundo.analisyswords.PalabrasClave;
import com.magik.mundo.analisyswords.Words;
import com.magik.mundo.controllers.RotationControlService;
import com.magik.mundo.data.ControlerData;
import com.magik.mundo.monitoring.ControlerDisplay;
import com.magik.services.WebServiceConnection;

public class WebActivity extends Activity implements OnTouchListener, Handler.Callback
{
    private WebView objWebView;
    private ImageButton btnAgregar;
    private ImageButton btnPk;
    private ImageButton btnIr;
    private ImageButton btnLenguaje;
    private EditText txtUrl;
    private final Handler handler = new Handler( this );
    private Thread thread;
    private Thread t;
    private String rta = "";
    private boolean sensorProcessGuardar;
    private boolean sensorProcess;
    private String CLASE_DYSPLAY = "MURL";
    private ControlerData data;
    private ControlerDisplay display;
    private PersistenceManager persistenceManager;
    private ArrayList<String> lecturas;
    private WebViewClient client;
    private static final int CLICK_ON_WEBVIEW = 1;
    private static final int CLICK_ON_URL = 2;
    private ArrayList<String> lenguajes;
    private boolean cargoplabras;
    private boolean cargoProfiles;
    private boolean cargolenguaje;
    private String[] recs;
    private long time;
    private InterfaceManager manager;
    private RotationControlService rotationService;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        cargoProfiles = false;
        cargolenguaje = false;
        setContentView( R.layout.activity_web );
        data = new ControlerData( );
        data.existDelete( CLASE_DYSPLAY );
        data.crearFile( CLASE_DYSPLAY, "Velocity (X,Y) ;Titulo;Url" );
        lecturas = new ArrayList<String>( );
        display = new ControlerDisplay( );
        objWebView = ( WebView )findViewById( R.id.webView );
        objWebView.setOnTouchListener( this );
        time = -1;
        Intent service = new Intent(getApplicationContext(), RotationControlService.class);
        startService(service);
        service = null;
        
        
        client = new WebViewClient( )
        {
            @Override
            public boolean shouldOverrideUrlLoading( WebView view, String url )
            {

                Toast.makeText( getApplicationContext( ), "palabras" + txtUrl.getText( ).toString( ), Toast.LENGTH_SHORT ).show( );
                System.out.println( txtUrl.getText( ).toString( ) );
                t = new Thread( )
                {
                	
                    @Override
                    public void run( )
                    {
                    	rotationService = RotationControlService.getInstance();
                        manager = InterfaceManager.getInstance( );
                        while( sensorProcess && manager.ismAct( ) && rotationService.isReading())
                        {
                            try
                            {
                                Log.v( "KELVIN", "lecturas " + lecturas.size( ) );
                                if( lecturas.size( ) > 5 )
                                {
                                    // Toast.makeText( getApplicationContext( ),
                                    // "Analizando pagina" + objWebView.getUrl(
                                    // ), Toast.LENGTH_SHORT ).show( );
                                    ArrayList<String> pals = new ArrayList<String>( );
                                    if( !connected( ) )
                                    {
                                        WebServiceConnection connection = WebServiceConnection.darInctancia( );
                                        String[] nombresParams = { "interes", "archivo" };
                                        Object[] params = new Object[2];
                                        params[ 0 ] = objWebView.getUrl( );
                                        params[ 1 ] = "LECTURA";
                                        String string = ( String )connection.accederServicio( WebServiceConnection.PALABRAS_URL, nombresParams, params );
                                        for( String s : string.split( ";" ) )
                                        {
                                            pals.add( s );
                                        }
                                        string = null;
                                        connection = null;
                                        nombresParams = null;
                                        params = null;

                                    }
                                    else
                                    {
                                        if( !cargoplabras )
                                        {
                                            Words w = new Words( cargoProfiles );
                                            cargoProfiles = w.cargoProfiles();
                                            boolean exito = w.startWords( objWebView.getUrl( ) );
                                            if( exito )
                                            {
                                                if( !cargolenguaje )
                                                {
                                                    lenguajes = w.getLenguaje( );
                                                    if( lenguajes != null && lenguajes.size( ) > 0 )
                                                    {
                                                        Vibrator vibrator = ( Vibrator )WebActivity.this.getSystemService( VIBRATOR_SERVICE );
                                                        vibrator.vibrate( 3000 );
                                                        cargolenguaje = true;
                                                        sensorProcess = false;
                                                    }
                                                }
                                                PalabrasClave palabras = PalabrasClave.darInstacia( );
                                                pals = palabras.getPalabras( );
                                                cargoplabras = true;
                                                w = null;
                                            }
                                            System.gc( );
                                        }
                                    }
                                    recomendaciones( pals );
                                }
                                sleep( 30000 );
                            }
                            catch( InterruptedException e )
                            {
                                e.printStackTrace( );
                            }
                            catch( Exception e )
                            {
                                e.printStackTrace( );
                            }
                        }
                    }
                };
                t.start( );
                handler.sendEmptyMessage( CLICK_ON_URL );
                return false;
            }
        };
        objWebView.setWebViewClient( client );
        objWebView.getSettings( ).setJavaScriptEnabled( true );

        txtUrl = ( EditText )findViewById( R.id.txtRuta );
        txtUrl.setText( "http://m.eltiempo.com" );

        sensorProcess = true;
        thread = new Thread( )
        {
            @Override
            public void run( )
            {
                while( sensorProcessGuardar )
                {
                    try
                    {
                        Log.v( "KELVIN", "Guardo" );
                        guardar( );
                        sleep( 120000 );
                    }
                    catch( InterruptedException e )
                    {
                        e.printStackTrace( );
                    }
                }
            }
        };
        thread.start( );

        inicializarBoton( );
        inicializarBotonCargar( );
        inicializarBotonLenguaje( );
        inicializarBotonPk( );
    }

    private boolean connected( )
    {
        boolean connected = false;
        ConnectivityManager manager = ( ConnectivityManager )getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo( );
        connected = activeNetworkInfo != null && activeNetworkInfo.isConnected( );
        System.out.println(connected);
        return connected;
    }

    protected void recomendaciones( ArrayList<String> palabras )
    {
        try
        {
            if( !connected( ) )
            {
                WebServiceConnection connection = WebServiceConnection.darInctancia( );
                String[] nombresParams = { "url", "interes" };
                Object[] params = { objWebView.getUrl( ), "LECTURA" };
                String[] nWords = ( ( String )connection.accederServicio( WebServiceConnection.RECOMEND_URL, nombresParams, params ) ).split( ";" );
                for( int i = 0; i < nWords.length; i++ )
                {
                    palabras.add( nWords[ i ] );
                }
                nombresParams = null;
                params = null;
                nWords = null;
            }
            String[] pals = new String[palabras.size( )];
            pals = palabras.toArray( pals );
            // TODO pals = new String[] { "Kelvin", "Tita", "Julio" };
            System.out.println( "ARREGLO: -------------------------------------------------- " + Arrays.deepToString( pals ) );
            if( pals.length > 0 )
            {
                persistenceManager = new PersistenceManager( getApplicationContext( ) );
                System.out.println( "BDDDDDDDDDDDDDDDDDDDDDDDDDDDD" );
                String urlTemp = objWebView.getUrl( );
                if( !persistenceManager.isFileInTable( urlTemp ) )
                {
                    persistenceManager.createDocument( urlTemp, PersistenceManager.HTML, objWebView.getTitle( ) );

                }
                sensorProcess = false;
                ArrayList<String> guardar = new ArrayList<String>( );
                for( String palabra : pals )
                {
                    if( !persistenceManager.isPKInTable( palabra, urlTemp ) )
                    {
                        guardar.add( palabra );
                    }
                }
                String[] g = new String[guardar.size( )];
                g = guardar.toArray( g );
                guardar = null;
                persistenceManager.savePalabrasClave( urlTemp, g );
                g = null;
                ArrayList<String> recomms = persistenceManager.recomendar( urlTemp );
                recs = new String[recomms.size( )];
                for( int i = 0; i < recs.length; i++ )
                {                	
                    recs[ i ] = recomms.get( i );
                }
                persistenceManager.saveRecommendations( objWebView.getUrl( ), recs );
                System.out.println("Recomendaciones: "+ Arrays.deepToString( recs ) );
                Vibrator vibrator = ( Vibrator )WebActivity.this.getSystemService( VIBRATOR_SERVICE );
                vibrator.vibrate( 1000 );

                pals = null;
                recomms = null;
                persistenceManager = null;
            }

        }
        catch( Exception e )
        {
            System.err.println( e.getMessage( ) );
        }
        System.gc( );
    }

    public void actualizarTextoPk( ArrayList<String> texto )
    {
        TextView text = ( TextView )findViewById( R.id.TextViewPk );
        text.setText( "" );
        if( texto != null && texto.size( ) > 0 )
        {
            for( int i = 0; i < texto.size( ); i++ )
            {
                String anterior = ( String )text.getText( );
                String cambio = texto.get( i ) + "\n" + anterior;
                text.setText( cambio );
            }
        }
        else
        {
            text.setText( "No se tiene cargado las Pk" );
        }
    }

    public void actualizarTextoLecturas( ArrayList<String> texto )
    {
        TextView text = ( TextView )findViewById( R.id.TextViewL );
        text.setText( "" );
        if( texto != null )
        {
            for( int i = 0; i < texto.size( ); i++ )
            {
                String anterior = ( String )text.getText( );
                String cambio = texto.get( i ) + "\n" + anterior;
                text.setText( cambio );
            }
        }
        else
        {
            text.setText( "No se tiene cargado el idioma" );
        }
    }

    public void guardar( )
    {
        data.writeToFile( rta, true, CLASE_DYSPLAY );
        data.writeToFile( rta, true, CLASE_DYSPLAY );
        rta = "";
        lecturas = new ArrayList<String>( );
        sensorProcessGuardar = false;
    }

    private void inicializarBoton( )
    {
        btnIr = ( ImageButton )findViewById( R.id.btnIr );
        btnIr.setOnClickListener( new View.OnClickListener( )
        {

            public void onClick( View v )
            {
            	Vibrator vibrator = ( Vibrator )WebActivity.this.getSystemService( VIBRATOR_SERVICE );
                vibrator.vibrate( 300 );
                cargolenguaje = false;
                cargoplabras = false;
                sensorProcess = true;
                sensorProcessGuardar = true;
                lecturas = new ArrayList<String>( );
                System.gc( );
                cargarRuta( );
            }
        } );
    }

    private void inicializarBotonCargar( )
    {
        btnAgregar = ( ImageButton )findViewById( R.id.button1 );
        btnAgregar.setEnabled( true );
        btnAgregar.setOnClickListener( new View.OnClickListener( )
        {

            @Override
            /*
             * (non-Javadoc)
             * 
             * @see android.view.View.OnClickListener#onClick(android.view.View)
             */
            public void onClick( View v )
            {
            	Vibrator vibrator = ( Vibrator )WebActivity.this.getSystemService( VIBRATOR_SERVICE );
                vibrator.vibrate( 300 );
                AlertDialog.Builder builder = new AlertDialog.Builder( WebActivity.this );
                builder.setTitle( "Recomendaciones" );
                String msg = "";
                if( recs != null && recs.length > 0 )
                {
                	for(String s: recs)
                	{
                		msg+=s+"\n\n";
                	}
                    builder.setMessage(msg);
                }
                else
                {
                    builder.setMessage( "No hay recomendaciones disponibles." );
                }
                builder.setCancelable( true );
                AlertDialog alert = builder.create( );
                alert.show( );
            }
        } );
    }

    private void inicializarBotonLenguaje( )
    {
        btnLenguaje = ( ImageButton )findViewById( R.id.btncargarlenguaje );
        btnLenguaje.setOnClickListener( new View.OnClickListener( )
        {

            public void onClick( View v )
            {
            	Vibrator vibrator = ( Vibrator )WebActivity.this.getSystemService( VIBRATOR_SERVICE );
                vibrator.vibrate( 300 );
                actualizarTextoLecturas( lenguajes );
            }
        } );
    }

    private void inicializarBotonPk( )
    {
        btnPk = ( ImageButton )findViewById( R.id.ButtonPk );
        btnPk.setOnClickListener( new View.OnClickListener( )
        {

            public void onClick( View v )
            {
            	Vibrator vibrator = ( Vibrator )WebActivity.this.getSystemService( VIBRATOR_SERVICE );
                vibrator.vibrate( 300 );
                PalabrasClave palabrasClave = PalabrasClave.darInstacia( );
                actualizarTextoPk( palabrasClave.getPalabras( ) );
            }
        } );
    }

    private void cargarRuta( )
    {
        String vRuta = txtUrl.getText( ).toString( );
        objWebView.loadUrl( vRuta );
    }

    /**
     * Muestra la actividad que permite ver los productoscreados
     */
    public boolean onTouchEvent( MotionEvent event )
    {
        return true;
    }

    @Override
    public boolean onTouch( View v, MotionEvent event )
    {
    	long time2 = 0;
    	if(event.getAction()==MotionEvent.ACTION_UP)
    	{   
    		time2 = System.currentTimeMillis();
        	if(time!=-1)
        	{
        		time2 -= time;
        	}
        	time = System.currentTimeMillis();
    	}
    	    	
    	    	
    	
        if( v.getId( ) == R.id.webView && event.getAction( ) == MotionEvent.ACTION_DOWN )
        {
            handler.sendEmptyMessageDelayed( CLICK_ON_WEBVIEW, 500 );
        }

        String captura = "";
        
        captura = display.velocityTrack( event );
        float valorx = 0;
        float valory = 0;
        if( captura != "" && captura != null )
        {
            String partexy[] = captura.split( ":" );
            String valores[] = partexy[ 1 ].split( ";" );
            valorx = Float.parseFloat( valores[ 0 ] );
            valory = Float.parseFloat( valores[ 1 ] );
        }
        int a = ( int )valorx;
        int b = ( int )valory;

        String rtaX = display.analizarVelocidadCorto( a, "X", time2 );
        String tipolecturax = display.darTipoLectura( );

        String rtaY = display.analizarVelocidadCorto( b, "Y", time2);
        String tipolecturay = display.darTipoLectura( );

        String tipoLectorua = "";

        if( Math.abs( a ) > Math.abs( b ) )
        {
            tipoLectorua = tipolecturax;
        }
        else
        {
            tipoLectorua = tipolecturay;
        }

        if( captura != "" )
        {
            rta += "\n";
            rta += objWebView.getTitle( ) + ";" + captura + ";" + rtaX + ";" + rtaY + ";" + objWebView.getUrl( );
            if( tipoLectorua.contains( "LECTURA" ) )
            {
                lecturas.add( rta );
            }

            String textoFrontal = tipoLectorua + ":(" + a + "," + b + ")";
            TextView text = ( TextView )findViewById( R.id.textView1 );
            String anterior = ( String )text.getText( );
            if( anterior.length( ) > 500 )
            {
                anterior = "";
            }
            String cambio = textoFrontal + "\n" + anterior;
            text.setText( cambio );

        }
        return false;
    }    

    @Override
    public boolean handleMessage( Message msg )
    {
        if( msg.what == CLICK_ON_URL )
        {
            lecturas = new ArrayList<String>( );
            
            cargolenguaje = false;
            cargoplabras = false;
            sensorProcess = true;
            sensorProcessGuardar = true;
            lenguajes = null;
            Toast.makeText( this, "WebView url" + msg, Toast.LENGTH_SHORT ).show( );
            handler.removeMessages( CLICK_ON_WEBVIEW );
            PalabrasClave palabrasClave = PalabrasClave.darInstacia( );
            palabrasClave.iniPalabras( );
            return true;
        }
        if( msg.what == CLICK_ON_WEBVIEW )
        {
            return true;
        }
        return false;
    }
    
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
    	stopService(new Intent(getApplicationContext(), RotationControlService.class));
    	super.onPause();
    }
    
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
    	stopService(new Intent(getApplicationContext(), RotationControlService.class));
    	super.onDestroy();
    }
}
