package com.magik.activities;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
    private boolean analizando;
    private boolean completo;
    private String[] recs;
    private long time;
    private InterfaceManager manager;
    private RotationControlService rotationService;
    private TextView txtLeft;
    private ArrayList<String> swipes;

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
        swipes = new ArrayList<String>( );
        display = new ControlerDisplay( );
        objWebView = ( WebView )findViewById( R.id.webView );
        objWebView.setOnTouchListener( this );
        time = -1;
        manager = InterfaceManager.getInstance( );
        txtLeft = ( TextView )findViewById( R.id.textView1 );
        analizando = false;
        completo = false;
        if( manager.ismGyro( ) )
        {
            Intent service = new Intent( getApplicationContext( ), RotationControlService.class );
            startService( service );
            service = null;
        }

        client = new WebViewClient( )
        {
            @Override
            public boolean shouldOverrideUrlLoading( WebView view, String url )
            {
                Toast.makeText( getApplicationContext( ), "Browsing: " + txtUrl.getText( ), Toast.LENGTH_SHORT ).show( );
                t = new Thread( )
                {

                    @Override
                    public void run( )
                    {

                    	
                        if( manager.ismGyro( ) )
                        {
                            rotationService = RotationControlService.getInstance( );
                            while( sensorProcess && manager.ismAct( ) && rotationService.isReading( ) )
                            {
                                try
                                {
                                	Log.v( "KELVIN", "lecturas " + lecturas.size( ) );
                                	Log.v( "KELVIN", "swipes: " + swipes.size() + " " + swipes.toString() );
                                    if( swipes.size( ) > 1 )
                                    {
                                        int cardinalidadLectura = cardinalidadLecturas( );
                                        int cardinalidadBusqueda = cardinalidadBusqueda( );
                                        int cardinalidadLecturarapida = cardinalidadLecturaRapida( );
                                        double analisis = (cardinalidadLectura + (cardinalidadLecturarapida/2))/(cardinalidadBusqueda + cardinalidadLectura + cardinalidadLecturarapida);
                                        System.out.println("Análisis: "+ analisis);
//                                        if( lecturas.size( ) > 5 )
                                        if(analisis>0.5)
                                        {
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
                                                    analizando = true;

                                                    Words w = new Words( cargoProfiles );
                                                    cargoProfiles = w.cargoProfiles( );
                                                    boolean exito = w.startWords( objWebView.getUrl( ) );
                                                    if( exito )
                                                    {
                                                        analizando = false;
                                                        completo = true;
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
                        else
                        {
                            while( sensorProcess && manager.ismAct( ) )
                            {
                                try
                                {
                                	Log.v( "KELVIN", "lecturas " + lecturas.size( ) );
                                	Log.v( "KELVIN", "swipes: " + swipes.size() + " " + swipes.toString() );
                                    if( swipes.size( ) > 1 )
                                    {                                    	
                                    	int cardinalidadLectura = cardinalidadLecturas( );
                                    	int cardinalidadBusqueda = cardinalidadBusqueda( );
                                    	int cardinalidadLecturarapida = cardinalidadLecturaRapida( );
                                    	double analisis = (cardinalidadLectura + (cardinalidadLecturarapida/2))/(cardinalidadBusqueda + cardinalidadLectura + cardinalidadLecturarapida);
                                    	if(analisis>0.5)
                                    	{
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
                                    					cargoProfiles = w.cargoProfiles( );
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
        System.out.println( connected );
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
            System.out.println( "ARREGLO: -------------------------------------------------- " + Arrays.deepToString( pals ) );
            if( pals.length > 0 )
            {
                persistenceManager = new PersistenceManager( getApplicationContext( ) );
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
                System.out.println( "Recomendaciones: " + Arrays.deepToString( recs ) );
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
            text.setText( "No se han cargado las Pk." );
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

            @Override
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
                    for( String s : recs )
                    {
                        msg += s + "\n\n";
                    }
                    builder.setMessage( msg );
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

            @Override
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

            @Override
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
    @Override
    public boolean onTouchEvent( MotionEvent event )
    {
        return true;
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

    @Override
    public boolean onTouch( View v, MotionEvent event )
    {
        GridLayout layout = ( GridLayout )findViewById( R.id.weblayout );
        ProgressBar bar = new ProgressBar( getApplicationContext( ), null, android.R.attr.progressBarStyleSmall );

        if( analizando )
        {
            txtLeft.setText( "Analizando..." );
            layout.addView( bar );
        }
        else if( completo )
        {
            txtLeft.setText( "Análisis completo." );
            layout.removeView( bar );
        }

        layout = null;
        bar = null;

        long time2 = 0;
        if( event.getAction( ) == MotionEvent.ACTION_UP )
        {
            time2 = System.currentTimeMillis( );
            if( time != -1 )
            {
                time2 -= time;
            }
            time = System.currentTimeMillis( );
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

        String rtaY = display.analizarVelocidadCorto( b, "Y", time2 );
        String tipolecturay = display.darTipoLectura( );

        String tipoLectura = "";

        if( Math.abs( a ) > Math.abs( b ) )
        {
            tipoLectura = tipolecturax;
        }
        else
        {
            tipoLectura = tipolecturay;
        }
        if(event.getAction() == MotionEvent.ACTION_UP)
        {
        	swipes.add( tipoLectura );
        }        
        if( captura != "" )
        {
            rta += "\n";
            rta += objWebView.getTitle( ) + ";" + captura + ";" + rtaX + ";" + rtaY + ";" + objWebView.getUrl( );
            if( tipoLectura.contains( "LECTURA" ) )
            {
                lecturas.add( rta );
                Toast.makeText( getApplicationContext( ), "Reading", Toast.LENGTH_SHORT ).show( );
            }
            /*
             * String textoFrontal = tipoLectorua + ":(" + a + "," + b + ")"; TextView text = ( TextView )findViewById( R.id.textView1 ); String anterior = ( String
             * )text.getText( ); if( anterior.length( ) > 500 ) { anterior = ""; } String cambio = textoFrontal + "\n" + anterior; text.setText( cambio );
             */

        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.os.Handler.Callback#handleMessage(android.os.Message)
     */
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
            analizando = false;
            completo = false;
            txtLeft.setText( "" );
            handler.removeMessages( CLICK_ON_WEBVIEW );
            swipes.clear();            
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
     * 
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause( )
    {
        stopService( new Intent( getApplicationContext( ), RotationControlService.class ) );
        super.onPause( );
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy( )
    {
        stopService( new Intent( getApplicationContext( ), RotationControlService.class ) );
        super.onDestroy( );
    }
}
