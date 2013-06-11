package com.magik.mundo.controllers;

import android.os.FileObserver;
import com.magik.mundo.data.ControlerData;

public class MyFileObserver extends FileObserver
{
    private String CLASE_FILE = "MFILE";
    public String absolutePath;
    private String name;
    private ControlerData data;
    private boolean directorio;
    private boolean creado;
    private String accesos;

    public MyFileObserver( String pName, String path, FileModificationService pServicio, boolean esDirectorio )
    {
        super( path, FileObserver.ALL_EVENTS );
        accesos = "";
        creado = false;
        data = new ControlerData( );
        absolutePath = path;
        name = pName;
        CLASE_FILE += name;
        directorio = esDirectorio;
    }

    @Override
    public void onEvent( int event, String path )
    {
        if( event == 32 )
        {
            accesos += absolutePath + " was opened\n";
            if( !directorio )
            {
                data.crearFile( CLASE_FILE, "Datos:\n" );
                creado = true;
            }
        }

        if( event == 1 )
        {
            accesos += absolutePath + " Data was read from a file\n";
        }

        if( event == 8 )
        {
            accesos += absolutePath + " open for writing, and closed it\n";
        }

        if( event == 512 )
        {
            accesos += absolutePath + " file was deleted from the monitored directory\n";
        }

        if( event == 32 )
        {
            accesos += absolutePath + " file or directory was opened\n";
        }

        if( ( FileObserver.CREATE & event ) != 0 )
        {
            accesos += absolutePath + "/" + name + " is created\n";
        }
        // a file or directory was opened
        if( ( FileObserver.OPEN & event ) != 0 )
        {
            accesos += name + " is opened\n";
        }
        // data was read from a file
        if( ( FileObserver.ACCESS & event ) != 0 )
        {
            accesos += absolutePath + "/" + name + " is accessed/read\n";
        }
        // data was written to a file
        if( ( FileObserver.MODIFY & event ) != 0 )
        {
            accesos += absolutePath + "/" + name + " is modified\n";
        }
        // someone has a file or directory open read-only, and closed it
        if( ( FileObserver.CLOSE_NOWRITE & event ) != 0 )
        {
            accesos += name + " is closed\n";
        }
        // someone has a file or directory open for writing, and closed it
        if( ( FileObserver.CLOSE_WRITE & event ) != 0 )
        {
            accesos += absolutePath + "/" + name + " is written and closed\n";
        }
        // a file was deleted from the monitored directory
        if( ( FileObserver.DELETE & event ) != 0 )
        {
            accesos += absolutePath + "/" + name + " is deleted\n";
        }
        // the monitored file or directory was deleted, monitoring effectively
        // stops
        if( ( FileObserver.DELETE_SELF & event ) != 0 )
        {
            accesos += absolutePath + "/" + " is deleted\n";
            // servicoGeneral.stopServiceProcess( );//intent );
        }
        // a file or subdirectory was moved from the monitored directory
        if( ( FileObserver.MOVED_FROM & event ) != 0 )
        {
            accesos += absolutePath + "/" + name + " is moved to somewhere " + "\n";
        }
        // a file or subdirectory was moved to the monitored directory
        if( ( FileObserver.MOVED_TO & event ) != 0 )
        {
            accesos += "File is moved to " + absolutePath + "/" + name + "\n";
        }
        // the monitored file or directory was moved; monitoring continues
        if( ( FileObserver.MOVE_SELF & event ) != 0 )
        {
            accesos += path + " is moved\n";
        }
        // Metadata (permissions, owner, timestamp) was changed explicitly
        if( ( FileObserver.ATTRIB & event ) != 0 )
        {
            accesos += absolutePath + "/" + name + " is changed " + "" + "(permissions, owner, timestamp)\n";
        }

        if( !directorio && creado )
        {
            data.writeToFile( accesos, true, CLASE_FILE );
        }
    }
}