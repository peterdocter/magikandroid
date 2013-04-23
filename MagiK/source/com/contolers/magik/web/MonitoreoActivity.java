package com.contolers.magik.web;

import net.sf.andpdf.pdfviewer.PdfFileSelectActivity;
import net.sf.andpdf.pdfviewer.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.analisys.words.Words;
import com.contolers.magik.file.FileModificationMonitor;
import com.example.magik.DisplayActivity;
import com.example.magik.monitoring.WebActivity;


public class MonitoreoActivity extends Activity 
{

    private Button btn_Web;
    private Button btn_File;
    private Button btn3;
    private Button btn4;
    
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_monitoreo );
        inicializarBotones( );
    }
    
    private void inicializarBotones()
    {
        btn_Web = ( Button )findViewById( R.id.webMonitoreo );
        
        btn_Web.setOnClickListener( new View.OnClickListener( )
        {
            // @Override
            public void onClick( View v )
            {
                startWebActivity();
            }
        } );
        
        btn_File = ( Button )findViewById( R.id.fileMonitoreo );
        btn_File.setOnClickListener( new View.OnClickListener( )
        {
            // @Override
            public void onClick( View v )
            {
                startFileActivity();
            }
        } );
        btn3 = ( Button )findViewById( R.id.button3 );
        btn3.setOnClickListener( new View.OnClickListener( )
        {
            // @Override
            public void onClick( View v )
            {
                
                startDisplayActivity();
            }
        } );
        btn4 = ( Button )findViewById( R.id.button4 );
        btn4.setOnClickListener( new View.OnClickListener( )
        {
            // @Override
            public void onClick( View v )
            {
                
                startWebMonitoreoActivity();
                Words w= new Words( );
            }
        } );
    }
    
    public void startWebActivity()
    {
        Intent web= new Intent(this, MonitoreoWebActivity.class);
        startActivity(web);
    }
    
    public void startFileActivity()
    {
        Intent file= new Intent(this, FileModificationMonitor.class);
        startActivity(file);
    }
    
    public void startDisplayActivity()
    {
        Intent file= new Intent(this, PdfFileSelectActivity.class);
        startActivity(file);
    }
    public void startWebMonitoreoActivity()
    {
        Intent file= new Intent(this, WebActivity.class);
        startActivity(file);
    }
    
}
