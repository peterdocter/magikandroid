package com.magik.activities;

import com.magik.R;
import com.magik.mundo.controllers.FileAccessLogStatic;
import com.magik.mundo.controllers.FileModificationService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//use asynctask background thread to detect file modification and then update the UI once there is 
//a new change detected
public class FileModificationMonitorActivity extends Activity
{
    private Button btn1;
    private Button btn2;
    private TextView text1;
    private boolean started = false;
    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.file_modification_monitor );
        btn1 = ( Button )findViewById( R.id.file_modification_monitor_btn_first );
        btn2 = ( Button )findViewById( R.id.file_modification_monitor_btn_second );
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
        refreshLog( );
    }


    public void refreshLog( )
    {
        text1.setText( "" );
    }

    public void clearLog( )
    {
        FileAccessLogStatic.accessLogMsg = "";
        text1.setText( "" );
    }
}
