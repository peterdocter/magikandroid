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
import android.widget.Toast;

import com.contolers.magik.data.ControlerData;
import com.data.bd.PersistenceManager;
import com.recomendacion.servicio.Servicio;

public class WebActivity extends Activity implements OnTouchListener, Handler.Callback
{
    private WebView objWebView;
    private Button btnAgregar;
    private EditText txtUrl;
    private final Handler handler = new Handler( this );
    private Thread thread;
    private Thread t; 
    private String rta = "";
    private boolean sensorProcess;
    private String CLASE_DYSPLAY = "MURL";
    private ControlerData data;
    private ControlerDisplay display;
    private PersistenceManager persistenceManager;
    private ArrayList<String> lecturas;

    private WebViewClient client;

    private static final int CLICK_ON_WEBVIEW = 1;
    private static final int CLICK_ON_URL = 2;


    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_web );
        data = new ControlerData( );
        data.existDelete( CLASE_DYSPLAY );
        data.crearFile( CLASE_DYSPLAY, "Velocity (X,Y) ;Titulo;Url" );
        lecturas = new ArrayList<String>();
        display = new ControlerDisplay( );
       
        


        objWebView = ( WebView )findViewById( R.id.webView );
        objWebView.setOnTouchListener( this );

        client = new WebViewClient( )
        {
            

			@Override
            public boolean shouldOverrideUrlLoading( WebView view, String url )
            {
                Toast.makeText( getApplicationContext( ), txtUrl.getText().toString(), Toast.LENGTH_SHORT ).show( );
                System.out.println(txtUrl.getText().toString());
                t = new Thread()
                {
                	@Override
                	public void run()
                	{
                		  while( sensorProcess )
                          {
                              try
                              {
                                  Log.v( "KELVIN", "Save Reads" );
                                  if(lecturas.size()>5)
                                  {
                                	 recomendaciones(); 
                                  }
                                  
                                  sleep( 10000 );
                              }
                              catch( InterruptedException e )
                              {
                                  e.printStackTrace( );
                              }
                          }
                	}
                };
                t.start();
                handler.sendEmptyMessage( CLICK_ON_URL );
                return false;
            }
        };
        objWebView.setWebViewClient( client );
        // objWebView.setVerticalScrollBarEnabled(false);
        objWebView.getSettings( ).setJavaScriptEnabled( true );

        txtUrl = ( EditText )findViewById( R.id.txtRuta );
        txtUrl.setText( "http://www.uniandes.edu.co" );
        // String customHtml = "<html><body><h1>Hello, WebView</h1></body></html>";
        // objWebView.loadData(customHtml, "text/html", "UTF-8");
        // objWebView.loadUrl("http://www.google.com.co");

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
                        Log.v( "KELVIN", "Guardo" );

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

        inicializarBoton( );
    }

    protected void recomendaciones() {
    	Servicio servicio = new Servicio();
    	String[] recs = servicio.getRecomendaciones(txtUrl.getText().toString());
    	persistenceManager = new PersistenceManager(this.getApplicationContext());
    	persistenceManager.saveRecommendations(txtUrl.getText().toString(), recs);
    	persistenceManager = null;
    	System.gc();
	}

	public void guardar( )
    {
        // data.crearFile( CLASE_DYSPLAY, "TIME;Velocity" );
        data.writeToFile( rta, true, CLASE_DYSPLAY );
        data.writeToFile( rta, true, CLASE_DYSPLAY );
        rta = "";
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
    // protected void startActividadBonos() {
    // Intent save = new Intent(this, ListaBonos.class);
    // startActivity(save);
    // }

    // @Override
    // public boolean onCreateOptionsMenu( Menu menu )
    // {
    // // Inflate the menu; this adds items to the action bar if it is present.
    // getMenuInflater( ).inflate( R.menu.activity_web, menu );
    // return true;
    // }
    // private VelocityTracker vTracker = null;
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
            String valores [] = partexy[1].split( ";" );
            valorx= Float.parseFloat( valores[0] );
            valory= Float.parseFloat( valores[1] );
        }
        int a = ( int )valorx;
        int b = ( int )valory;
        
        String rtaX = display.analizarVelocidad( a, "X" );
        String rtaY = display.analizarVelocidad( b, "Y" );
        
        if( captura != "" )
        {
            rta += "\n";
            rta += objWebView.getTitle( ) + ";" + captura + ";"+rtaX+";"+rtaY+";" + objWebView.getUrl( );
            if(rta.contains("LECTURA"));
            {
            	lecturas.add(rta);            	
            }
            
        }
        return false;
    }

    @Override
    public boolean handleMessage( Message msg )
    {
        if( msg.what == CLICK_ON_URL )
        {
            handler.removeMessages( CLICK_ON_WEBVIEW );
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
