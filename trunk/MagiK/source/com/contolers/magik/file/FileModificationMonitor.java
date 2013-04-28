package com.contolers.magik.file;

import net.sf.andpdf.pdfviewer.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//use asynctask background thread to detect file modification and then update the UI once there is 
//a new change detected
public class FileModificationMonitor extends Activity
{
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private TextView text1;
    private boolean started = false;
    private boolean pmStarted = false;

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
//        LocalBroadcastManager.getInstance( this ).registerReceiver( mMessageReceiver, new IntentFilter( "action" ) );
        setContentView( R.layout.file_modification_monitor );
        btn1 = ( Button )findViewById( R.id.file_modification_monitor_btn_first );
        btn2 = ( Button )findViewById( R.id.file_modification_monitor_btn_second );
        btn3 = ( Button )findViewById( R.id.file_modification_monitor_btn_third );
        btn4 = ( Button )findViewById( R.id.file_modification_monitor_btn_forth );
        int screenWidth = this.getWindowManager( ).getDefaultDisplay( ).getWidth( );
        btn1.setWidth( screenWidth / 4 );
        text1 = ( TextView )findViewById( R.id.file_modification_monitor_log );
        if( started )
        {
            btn1.setEnabled( true );
        }
        else
        {
            btn1.setEnabled( false );
        }

        btn1.setOnClickListener( new View.OnClickListener( )
        {
            @Override
            public void onClick( View v )
            {
                
                Intent intent = new Intent( getApplicationContext( ), FileModificationService.class );
                startService( intent );
                btn1.setEnabled( false );
                started = true;
            }
        } );
        btn2.setWidth( screenWidth / 4 );
        btn2.setOnClickListener( new View.OnClickListener( )
        {
            @Override
            public void onClick( View v )
            {
                stopService( new Intent( getApplicationContext( ), FileModificationService.class ) );
                btn1.setEnabled( true );
                started = false;
            }
        } );
        btn3.setWidth( screenWidth / 4 );
        btn3.setOnClickListener( new View.OnClickListener( )
        {
            @Override
            public void onClick( View v )
            {
                text1.setText( "" );// FileAccessLogStatic.accessLogMsg );
            }
        } );
        btn4.setWidth( screenWidth / 4 );
        btn4.setOnClickListener( new View.OnClickListener( )
        {
            @Override
            public void onClick( View v )
            {
                clearLog( );
            }
        } );
        refreshLog( );
    }

//    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver( )
//    {
//        @Override
//        public void onReceive( Context context, Intent intent )
//        {
//            String action = intent.getAction( );
//            String currentSpeed = intent.getStringExtra( "action" );
//            Toast.makeText( getApplicationContext( ), currentSpeed, Toast.LENGTH_SHORT ).show( );
//            startProcessMonitor( );
//
//        }
//    };

//    public void startProcessMonitor( )
//    {
//        Intent intent = new Intent(this, ProcessMonitor.class);
//        startService( intent );
//        btn1.setEnabled( true );
//        pmStarted = true;
//    }
//
//    public void stopProcessMonitor( )
//    {
//        if( !pmStarted )
//        {
//            stopService( new Intent( getApplicationContext( ), ProcessMonitor.class ) );
//            btn1.setEnabled( true );
//            pmStarted = true;
//        }
//    }

    public void refreshLog( )
    {
        text1.setText( "" );// FileAccessLogStatic.accessLogMsg );
    }

    public void clearLog( )
    {
        FileAccessLogStatic.accessLogMsg = "";
        text1.setText( "" );
    }
}
