package com.magik.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.magik.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;


public class MagikActivity extends Activity
{

    private Button btn_Web_Back;
    private Button btn_File_Back;
    private Button btn_Pdf_Reader;
    private Button btn_Web_Reader;
    private Button btn_settings;

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
        btn_Web_Back = ( Button )findViewById( R.id.webMonitoreo );

        btn_Web_Back.setOnClickListener( new View.OnClickListener( )
        {
            // @Override
            public void onClick( View v )
            {
                startWebBackGroundActivity( );
            }
        } );

        btn_File_Back = ( Button )findViewById( R.id.fileMonitoreo );
        btn_File_Back.setOnClickListener( new View.OnClickListener( )
        {
            // @Override
            public void onClick( View v )
            {
                startFileBackGroundActivity( );
            }
        } );
        btn_Pdf_Reader = ( Button )findViewById( R.id.button3 );
        btn_Pdf_Reader.setOnClickListener( new View.OnClickListener( )
        {
            // @Override
            public void onClick( View v )
            {

                startPdfReaderActivity( );
            }
        } );
        btn_Web_Reader = ( Button )findViewById( R.id.button4 );
        btn_Web_Reader.setOnClickListener( new View.OnClickListener( )
        {
            // @Override
            public void onClick( View v )
            {

                startWebReaderActivity( );
            }
        } );
        btn_settings = (Button)findViewById(R.id.settings);
        btn_settings.setOnClickListener(new View.OnClickListener() {
			/*
			 * (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick(View v) {
				showSettings();
				
			}
		});
        
        
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
    

    public void startWebBackGroundActivity( )
    {
        Intent web = new Intent( this, MonitoreoWebActivity.class );
        startActivity( web );
    }

    public void startFileBackGroundActivity( )
    {
        Intent file = new Intent( this, FileModificationMonitorActivity.class );
        startActivity( file );
    }

    public void startPdfReaderActivity( )
    {
        // Intent file= new Intent(this, PdfFileSelectActivity.class);
        Intent file = new Intent( this, PDFReaderAct.class );
        startActivity( file );
    }
    public void startWebReaderActivity( )
    {
        Intent file = new Intent( this, WebActivity.class );
        startActivity( file );
    }
    
    public void showSettings()
    {
    	Intent intent = new Intent(this, SettingsActivity.class);
    	startActivity(intent);
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
