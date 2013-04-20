package com.example.magik;

import java.io.File;

import net.sf.andpdf.pdfviewer.R;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.Toast;

import com.contolers.magik.data.ControlerData;
import com.example.magik.monitoring.ControlerDisplay;

public class DisplayActivity extends Activity
{
    private String CLASE_DYSPLAY = "MDYSPLAY";
    private ControlerData data;
    private ControlerDisplay display;
    private Thread thread;
    private String rta;
    private boolean sensorProcess;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        data = new ControlerData( );
        data.existDelete( CLASE_DYSPLAY );

        display = new ControlerDisplay( );
        sensorProcess = true;
        rta = "";
        setContentView( R.layout.activity_display );
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

        File file = new File( "/sdcard/Download/bases.pdf" );

        if( file.exists( ) )
        {
            Uri path = Uri.fromFile( file );
            Intent intent = new Intent( Intent.ACTION_VIEW );
            
            intent.setDataAndType( path, "application/pdf" );
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );

            try
            {
                startActivity( intent );
            }
            catch( ActivityNotFoundException e )
            {
                Toast.makeText( DisplayActivity.this, "No Application Available to View PDF", Toast.LENGTH_SHORT ).show( );
            }
        }
    }


    public void guardar( )
    {
        data.crearFile( CLASE_DYSPLAY, "TIME;Velocity" );
        data.writeToFile( rta, true, CLASE_DYSPLAY );
        rta = "";
    }

    // private VelocityTracker vTracker = null;
    public boolean onTouchEvent( MotionEvent event )
    {
        String captura = "";
        captura = display.velocityTrack( event );
        if( captura != "" )
        {
            rta += "\n";
            rta += captura;
        }
        return true;
    }

}
