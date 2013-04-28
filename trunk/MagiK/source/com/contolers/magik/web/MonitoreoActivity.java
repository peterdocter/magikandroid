package com.contolers.magik.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sf.andpdf.pdfviewer.R;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.contolers.magik.file.FileModificationMonitor;
import com.example.magik.monitoring.WebActivity;
import com.radaee.reader.PDFReaderAct;

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

    private void inicializarBotones( )
    {
        btn_Web = ( Button )findViewById( R.id.webMonitoreo );

        btn_Web.setOnClickListener( new View.OnClickListener( )
        {
            // @Override
            public void onClick( View v )
            {
                startWebActivity( );
            }
        } );

        btn_File = ( Button )findViewById( R.id.fileMonitoreo );
        btn_File.setOnClickListener( new View.OnClickListener( )
        {
            // @Override
            public void onClick( View v )
            {
                startFileActivity( );
            }
        } );
        btn3 = ( Button )findViewById( R.id.button3 );
        btn3.setOnClickListener( new View.OnClickListener( )
        {
            // @Override
            public void onClick( View v )
            {

                startDisplayActivity( );
            }
        } );
        btn4 = ( Button )findViewById( R.id.button4 );
        btn4.setOnClickListener( new View.OnClickListener( )
        {
            // @Override
            public void onClick( View v )
            {

                startWebMonitoreoActivity( );
            }
        } );
        
        File direct = new File(Environment.getExternalStorageDirectory() + "/Magik");
        if(!direct.exists())
         {
             if(direct.mkdir());

         }
        direct = null;
        direct = new File(Environment.getExternalStorageDirectory() + "/Magik/profiles");

        if(!direct.exists())
         {
             if(direct.mkdir());

         }
        copyAssets(direct.getAbsolutePath());
        direct = null;
        System.gc();
        

    }
    

    public void startWebActivity( )
    {
        Intent web = new Intent( this, MonitoreoWebActivity.class );
        startActivity( web );
    }

    public void startFileActivity( )
    {
        Intent file = new Intent( this, FileModificationMonitor.class );
        startActivity( file );
    }

    public void startDisplayActivity( )
    {
        // Intent file= new Intent(this, PdfFileSelectActivity.class);
        Intent file = new Intent( this, PDFReaderAct.class );
        startActivity( file );
    }
    public void startWebMonitoreoActivity( )
    {
        Intent file = new Intent( this, WebActivity.class );
        startActivity( file );
    }
    
    private void copyAssets(String path) {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("profiles");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        for(String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
              in = assetManager.open("profiles/"+filename);
              out = new FileOutputStream(path+"/"+filename);
              copyFile(in, out);
              in.close();
              in = null;
              out.flush();
              out.close();
              out = null;
            } catch(IOException e) {
                System.out.println(e.getMessage());
            }       
        }
    }
    
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
          out.write(buffer, 0, read);
        }
    }

}
