package com.magik.mundo.monitoring;

import java.util.ArrayList;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

public class ControlerDisplay
{
    public final static String LECTURA = "LECTURA";
    public final static String LECTURA_RAPIDA = "LECTURA_RAPIDA";
    public final static String BUSQUEDA = "BUSQUEDA";

    private VelocityTracker vTracker;
    private ArrayList<Float> valoresX;
    private ArrayList<Float> valoresY;
    private String tipoLectura;

    public ControlerDisplay( )
    {
        valoresX = new ArrayList<Float>( );
        valoresY = new ArrayList<Float>( );
        vTracker = VelocityTracker.obtain( );
        tipoLectura = "";
    }

    public String velocityTrack( MotionEvent event )
    {
        int action = event.getAction( );
        switch( action )
        {
            case MotionEvent.ACTION_DOWN:
                valoresX = new ArrayList<Float>( );
                valoresY = new ArrayList<Float>( );
                if( vTracker == null )
                {
                    vTracker = VelocityTracker.obtain( );
                }
                else
                {
                    vTracker.clear( );
                }
                vTracker.addMovement( event );
                break;
            case MotionEvent.ACTION_MOVE:
                vTracker.addMovement( event );
                vTracker.computeCurrentVelocity( 1000 );
                valoresX.add( vTracker.getXVelocity( ) );
                valoresY.add( vTracker.getYVelocity( ) );
                break;
            case MotionEvent.ACTION_UP:
                return calcularMovimiento( );
            case MotionEvent.ACTION_CANCEL:
                vTracker.recycle( );
                break;
        }
        return "";
    }

    public String calcularMovimiento( )
    {
        double valorPromedioX = 0;
        int i = 0;
        for( i = 0; i < valoresX.size( ); i++ )
        {
            valorPromedioX += valoresX.get( i );

        }
        if( valorPromedioX != 0 )
        {
            valorPromedioX = valorPromedioX / i;
        }
        else
        {
            valorPromedioX = 0;
        }

        double valorPromedioY = 0;
        int j = 0;
        for( j = 0; j < valoresY.size( ); j++ )
        {
            valorPromedioY += valoresY.get( j );

        }
        if( valorPromedioY != 0 )
        {
            valorPromedioY = valorPromedioY / j;
        }
        else
        {
            valorPromedioY = 0;
        }

        return "X,Y:"+valorPromedioX + ";" + valorPromedioY;

    }

    public String analizarVelocidad( int valor, String sentido )
    {
        String direccion;

        if( valor >= 0 )
        {
            if( sentido.equals( "X" ) )
            {
                direccion = "DERECHA";
            }
            else
            {
                direccion = "ABAJO";
            }

        }
        else
        {
            if( sentido.equals( "X" ) )
            {
                direccion = "IZQUIERDA";
            }
            else
            {
                direccion = "ARRIBA";
            }
        }
        if( valor < 100 && valor > -100 )
        {
            tipoLectura = LECTURA;
            return LECTURA + ";" +direccion + ";" + valor + ";" + sentido;
        }
        else if( valor >= 100 && valor < 1500 )
        {
            tipoLectura = LECTURA_RAPIDA ;
            return LECTURA_RAPIDA + ";" +direccion + ";" + valor + ";" + sentido;

        }
        else
        {
            tipoLectura = BUSQUEDA;
            return BUSQUEDA + ";" +direccion + ";" + valor + ";" + sentido;

        }
    }
    
    public String analizarVelocidadCorto( int valor, String sentido )
    {
        String direccion;

        if( valor >= 0 )
        {
            if( sentido.equals( "X" ) )
            {
                direccion = "DERECHA";
            }
            else
            {
                direccion = "ABAJO";
            }

        }
        else
        {
            if( sentido.equals( "X" ) )
            {
                direccion = "IZQUIERDA";
            }
            else
            {
                direccion = "ARRIBA";
            }
        }
        if( valor < 100 && valor > -100 )
        {
            tipoLectura = LECTURA;
            return  valor + "";
        }
        else if( valor >= 100 && valor < 1500 )
        {
            tipoLectura = LECTURA_RAPIDA ;
            return valor + "";

        }
        else
        {
            tipoLectura = BUSQUEDA;
            return  valor + "";

        }
    }
    
    public String darTipoLectura()
    {
        return tipoLectura;
    }
}
