package com.example.magik.monitoring;

import java.util.ArrayList;

import net.sf.andpdf.pdfviewer.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.analisys.words.PalabrasClave;
import com.analisys.words.Words;
import com.contolers.magik.data.ControlerData;
import com.data.bd.PersistenceManager;
import com.recomendacion.servicio.Servicio;

public class WebActivity extends Activity implements OnTouchListener, Handler.Callback
{
    private WebView objWebView;
    private Button btnAgregar;
    private Button btnLenguaje;
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
    private boolean cargoClaves;
    private boolean cargolenguaje;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        cargoClaves = false;
        cargolenguaje = false;
        setContentView( R.layout.activity_web );
        data = new ControlerData( );
        data.existDelete( CLASE_DYSPLAY );
        data.crearFile( CLASE_DYSPLAY, "Velocity (X,Y) ;Titulo;Url" );
        lecturas = new ArrayList<String>( );
        display = new ControlerDisplay( );
        objWebView = ( WebView )findViewById( R.id.webView );
        objWebView.setOnTouchListener( this );

        client = new WebViewClient( )
        {

            @Override
            public boolean shouldOverrideUrlLoading( WebView view, String url )
            {
                cargoplabras  = false;
                Toast.makeText( getApplicationContext( ),"palabras" +txtUrl.getText( ).toString( ), Toast.LENGTH_SHORT ).show( );
                System.out.println( txtUrl.getText( ).toString( ) );
                t = new Thread( )
                {
                    @Override
                    public void run( )
                    {
                        while( sensorProcess )
                        {
                            try
                            {
                                Log.v( "KELVIN", "lecturas "+lecturas.size( ) );
                                if( lecturas.size( ) > 5 )
                                {
                                    if( !cargoplabras )
                                    {
                                        Words w = new Words( objWebView.getUrl( ) );
                                        if( !cargolenguaje )
                                        {
                                            lenguajes = w.getLenguaje( );
                                            if( lenguajes!= null && lenguajes.size( )>0 )
                                            {
                                                cargolenguaje = true;
                                                sensorProcess = false;
                                            }
                                        }
                                        PalabrasClave palabras = PalabrasClave.darInstacia( );
                                        recomendaciones( palabras.getPalabras( ) );
                                        cargoplabras = true;
                                        w = null;
                                        System.gc( );
                                    }
                                }

                                sleep( 30000 );
                            }
                            catch( InterruptedException e )
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
    }

    protected void recomendaciones( ArrayList<String> palabras )
    {
        PalabrasClave palabrasClave = PalabrasClave.darInstacia( );
        Servicio servicio = new Servicio( );
        String[] recs = servicio.getRecomendaciones( objWebView.getUrl( ));
        persistenceManager = new PersistenceManager( this.getApplicationContext( ) );
        persistenceManager.saveRecommendations( objWebView.getUrl( ), recs );
//        Camabiar a un array list la entrada de recommendaciones
//        persistenceManager.saveRecommendations( objWebView.getUrl( ), palabras );
        persistenceManager = null;
        System.gc( );
    }
    
    public void actualizarTextoLecturas(ArrayList<String> texto)
    {
        TextView text = (TextView) findViewById(R.id.TextViewL);
        if( texto != null )
        {
            for( int i = 0; i < texto.size( ); i++ )
            {
                text.clearComposingText( );
                text.setText( texto.get( i ) );
            }
        }
        else
        {
            text.clearComposingText( );
            text.setText( "No se tiene cargado el idioma" );
        }
    }

    public void guardar( )
    {
        // data.crearFile( CLASE_DYSPLAY, "TIME;Velocity" );
        data.writeToFile( rta, true, CLASE_DYSPLAY );
        data.writeToFile( rta, true, CLASE_DYSPLAY );
        rta = "";
        lecturas = new ArrayList<String>( );
        sensorProcessGuardar = false;
    }

    private void inicializarBoton( )
    {
        btnAgregar = ( Button )findViewById( R.id.btnIr );
        btnAgregar.setOnClickListener( new View.OnClickListener( )
        {

            public void onClick( View v )
            {
                cargarRuta( );
            }
        } 
        );
    }
    
    private void inicializarBotonCargar( )
    {
        btnAgregar = ( Button )findViewById( R.id.button1 );
        btnAgregar.setOnClickListener( new View.OnClickListener( )
        {

            public void onClick( View v )
            {
                Words w = new Words( objWebView.getUrl( ) );
            }
        } 
        );
    }
    
    private void inicializarBotonLenguaje( )
    {
        btnLenguaje = ( Button )findViewById( R.id.btncargarlenguaje );
        btnLenguaje.setOnClickListener( new View.OnClickListener( )
        {

            public void onClick( View v )
            {
                actualizarTextoLecturas( lenguajes );
            }
        } 
        );
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

        // String captura = "";
        // captura = display.velocityTrack( event ) + objWebView.getUrl( );;
        // if( captura != "" )
        // {
        // rta += "\n";
        // rta += captura;
        // }
        return true;
    }

    @Override
    public boolean onTouch( View v, MotionEvent event )
    {
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

        String rtaX = display.analizarVelocidadCorto( a, "X" );
        String tipolecturax = display.darTipoLectura( );
        
        String rtaY = display.analizarVelocidadCorto( b, "Y" );
        String tipolecturay = display.darTipoLectura( );

        String tipoLectorua= "";
        
        if(Math.abs( a )>Math.abs( b ) )
        {
            tipoLectorua = tipolecturax;
        }else
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
            
            String textoFrontal =  tipoLectorua + ":(" +a + "," + b + ")";
            TextView text = (TextView) findViewById(R.id.textView1);
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
            cargolenguaje= false;
            lenguajes = null;
            Toast.makeText( this, "WebView url" + msg, Toast.LENGTH_SHORT ).show( );
            handler.removeMessages( CLICK_ON_WEBVIEW );
            sensorProcess = true;
            sensorProcessGuardar = true;
            return true;
        }
        if( msg.what == CLICK_ON_WEBVIEW )
        {
            Toast.makeText( this, "WebView clicked" + msg, Toast.LENGTH_SHORT ).show( );
            return true;
        }
        return false;
    }
}
