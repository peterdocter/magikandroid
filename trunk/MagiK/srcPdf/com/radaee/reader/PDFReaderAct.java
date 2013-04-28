package com.radaee.reader;

import java.io.File;
import java.util.ArrayList;

import net.sf.andpdf.pdfviewer.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.contolers.magik.data.ControlerData;
import com.data.bd.PersistenceManager;
import com.example.magik.monitoring.DatosDisplay;
import com.magik.tasks.SendPDFTask;
import com.radaee.grid.PDFGridItem;
import com.radaee.grid.PDFGridView;
import com.radaee.pdf.Document;
import com.radaee.pdf.Global;
import com.radaee.reader.PDFReader.PDFReaderListener;
import com.radaee.view.PDFVPage;
import com.radaee.view.PDFViewThumb.PDFThumbListener;
import com.recomendacion.servicio.WebServiceConnection;

public class PDFReaderAct extends Activity implements OnItemClickListener, OnClickListener, PDFReaderListener, PDFThumbListener
{
    private PDFGridView m_vFiles = null;
    private PDFReader m_reader = null;
    private PDFThumbView m_thumb = null;
    private RelativeLayout m_layout;
    private Document m_doc = new Document( );
    private Button btn_close;
    private Button btn_pal;
    private Button btn_rec;

    private boolean m_set = false;
    private ArrayList<String> recs;
    private ArrayList<String> palabras;

    private Thread thread;
    private Thread recommendationsThread;

    private String CLASE_DYSPLAY = "";
    private ControlerData data;
    private DatosDisplay datosDisplay;
    
    private boolean sensorProcess;
    private PersistenceManager persistenceManager;
	private String path;

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        path = "";
        data = new ControlerData( );
        palabras = new ArrayList<String>();
        recs = new ArrayList<String>();
        sensorProcess = true;
        recommendationsThread = new Thread( )
        {
            @Override
            public void run( )
            {
                
                while( sensorProcess )
                {
                    try
                    {
                        Log.v( "KELVIN", "Save Reads" );
                        datosDisplay = DatosDisplay.darInstacia( );
                        Log.d("Lecturas", String.valueOf(datosDisplay.getLecturas().size()));
                        if( datosDisplay.getLecturas( ).size( ) > 5 )
                        {
                        	Log.d("Recomendaciones", "Hizo el thread");
                            recomendaciones( );
                            palabrasClave();                            
                            sensorProcess = false;
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
        recommendationsThread.start( );

        Global.Init( this );
        m_vFiles = new PDFGridView( this, null );
        m_vFiles.PDFSetRootPath( "/mnt" );
        m_vFiles.setOnItemClickListener( this );
        setContentView( m_vFiles );
        m_layout = ( RelativeLayout )LayoutInflater.from( this ).inflate( R.layout.reader, null );
        m_reader = ( PDFReader )m_layout.findViewById( R.id.view );
        m_thumb = ( PDFThumbView )m_layout.findViewById( R.id.thumbs );
        LinearLayout bar_cmd = ( LinearLayout )m_layout.findViewById( R.id.bar_cmd );
        //LinearLayout bar_act = ( LinearLayout )m_layout.findViewById( R.id.bar_act );
        //LinearLayout bar_find = ( LinearLayout )m_layout.findViewById( R.id.bar_find );
        btn_close = ( Button )bar_cmd.findViewById( R.id.btn_close );        
        btn_pal = (Button)bar_cmd.findViewById(R.id.btnPal);
        btn_rec = (Button)bar_cmd.findViewById(R.id.btnRec);
        btn_rec.setOnClickListener(this);
        btn_pal.setOnClickListener(this);
        
    }

    public void guardar( )
    {
        DatosDisplay datos = DatosDisplay.darInstacia( );
        ArrayList<String> respuestax = datos.getAnalisiX( );
        ArrayList<String> respuestay = datos.getAnalisiY( );
        
        String rtax= "";
        String rtay= "";
        for( int i = 0; i < respuestax.size( ); i++ )
        {
            rtax = rtax + respuestax.get( i ) + "\n";
        }
        for( int i = 0; i < respuestay.size( ); i++ )
        {
            rtay = rtay + respuestay.get( i ) + "\n";
        }
        String rta = rtax + "\n" + rtay;
        // data.crearFile( CLASE_DYSPLAY, "TIME;Velocity" );
        if( respuestax.size( )>0  )
        {
            data.writeToFile( rta, true, CLASE_DYSPLAY );
            data.writeToFile( rta, true, CLASE_DYSPLAY );
        }
        rta = "";
    }

    
   

    protected void onDestroy( )
    {
        // m_vFiles.close();
        if( m_vFiles != null )
        {
            m_vFiles.close( );
            m_vFiles = null;
        }
        if( m_thumb != null )
        {
            m_thumb.thumbClose( );
            m_thumb = null;
        }
        if( m_reader != null )
            m_reader.PDFClose( );
        if( m_doc != null )
            m_doc.Close( );
        Global.RemoveTmp( );
        super.onDestroy( );
    }

    public void onItemClick( AdapterView<?> arg0, View arg1, int arg2, long arg3 )
    {
    	if( arg0 == m_vFiles )
        {
            PDFGridItem item = ( PDFGridItem )arg1;
           
            if( item.is_dir( ) )
            {
            	path+=item.get_name()+"/";
                m_vFiles.PDFGotoSubdir( item.get_name( ) );
            }
            else
            {
                m_doc.Close( );
                path+=item.get_name();
                
                
                String datos[] = item.get_name( ).split( "/" );
                CLASE_DYSPLAY = CLASE_DYSPLAY + datos[ datos.length - 1 ];
                int ret = item.open_doc( m_doc, null );
                switch( ret )
                {
                    case -1:// need input password
                        finish( );
                        break;
                    case -2:// unknown encryption
                        finish( );
                        break;
                    case -3:// damaged or invalid format
                        finish( );
                        break;
                    case -10:// access denied or invalid file path
                        finish( );
                        break;
                    case 0:// succeeded, and continue
                        m_reader.PDFOpen( m_doc, this );
                        break;
                    default:// unknown error
                        finish( );
                        break;
                }
                m_thumb.thumbOpen( m_reader.PDFGetDoc( ), this );
                setContentView( m_layout );
                
                data.existDelete( CLASE_DYSPLAY );
                data.crearFile( CLASE_DYSPLAY, "Acción;Dirección;Velocidad (pixeles/seg);Eje" );
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
                
            }
        }
        else
        {
        }
    }

    private void onSelect( )
    {
        m_set = !m_set;
        m_reader.PDFSetSelect( );
    }

  
    public void onClick( View v )
    {
        switch( v.getId( ) )
        {
            case R.id.btn_close:
                m_thumb.thumbClose( );
                m_reader.PDFClose( );
                if( m_doc != null )
                    m_doc.Close( );
                setContentView( m_vFiles );
                break;
            case R.id.btnRec:
            	actualizarTextoLecturas(recs);
            	break;
            case R.id.btnPal:
            	actualizarTextoLecturas(palabras);
            	break;
                
              
        }
    }

    public void OnPageClicked( int pageno )
    {
        m_reader.PDFGotoPage( pageno );
    }

    public void OnPageChanged( int pageno )
    {
        m_thumb.thumbGotoPage( pageno );
    }

    public void OnAnnotClicked( PDFVPage vpage, int annot )
    {
    }
    
    
    private void palabrasClave()
    {
    	File f = new File(path);
        SendPDFTask task = new SendPDFTask();
        try {
        	Object[] params = new Object[2];
        	params[0] = f;
        	params[1] = WebServiceConnection.PALABRAS_FILE; 
			task.execute(params);
			String resp = task.get();
			System.out.println("Respuesta: "+resp);
			if(resp.contains(";"))
			{
				String[] recArr  = resp.split(";");
				for(String rec: recArr)
				{
					palabras.add(rec);
				}
			}
			else
			{
				palabras.add(resp);
			}			
			params = null;			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
        f = null;
        task = null;
        System.gc();
    }
    
    private void recomendaciones( )
    {
    	File f = new File(path);
        SendPDFTask task = new SendPDFTask();
        try {
        	Object[] params = new Object[2];
        	params[0] = f;
        	params[1] = WebServiceConnection.RECOMEND_FILE; 
			task.execute(params);
			String resp = task.get();
			System.out.println("Respuesta: "+resp);
			if(resp.contains(";"))
			{
				String[] recArr  = resp.split(";");
				for(String rec: recArr)
				{
					recs.add(rec);
				}
				persistenceManager = new PersistenceManager( this.getApplicationContext( ) );
		        persistenceManager.saveRecommendations( CLASE_DYSPLAY, recArr );
		        persistenceManager = null;
			}
			else
			{
				recs.add(resp);
			}	        
			params = null;			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
        f = null;
        task = null;
        System.gc();
    }
    
    
    public void OnOpenURI( String uri )
    {
    }

    public void OnOpenMovie( String path )
    {
    }

    public void OnOpenSound( int[] paras, String path )
    {
    }

    public void OnOpenAttachment( String path )
    {
    }

    public void OnOpen3D( String path )
    {
    }

    public void OnSelectEnd( String text )
    {
        LinearLayout layout = ( LinearLayout )LayoutInflater.from( this ).inflate( R.layout.dlg_text, null );
        final RadioGroup rad_group = ( RadioGroup )layout.findViewById( R.id.rad_group );
        final String sel_text = text;

        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setPositiveButton( "OK", new DialogInterface.OnClickListener( )
        {
            public void onClick( DialogInterface dialog, int which )
            {
                if( rad_group.getCheckedRadioButtonId( ) == R.id.rad_copy )
                    Toast.makeText( PDFReaderAct.this, "todo copy text:" + sel_text, Toast.LENGTH_SHORT ).show( );
                else if( m_reader.PDFCanSave( ) )
                {
                    boolean ret = false;
                    if( rad_group.getCheckedRadioButtonId( ) == R.id.rad_highlight )
                        ret = m_reader.PDFSetSelMarkup( 0 );
                    else if( rad_group.getCheckedRadioButtonId( ) == R.id.rad_underline )
                        ret = m_reader.PDFSetSelMarkup( 1 );
                    else if( rad_group.getCheckedRadioButtonId( ) == R.id.rad_strikeout )
                        ret = m_reader.PDFSetSelMarkup( 2 );
                    if( !ret )
                        Toast.makeText( PDFReaderAct.this, "add annotation failed!", Toast.LENGTH_SHORT ).show( );
                }
                else
                    Toast.makeText( PDFReaderAct.this, "can't write or encrypted!", Toast.LENGTH_SHORT ).show( );
                onSelect( );
                dialog.dismiss( );
            }
        } );
        builder.setNegativeButton( "Cancel", new DialogInterface.OnClickListener( )
        {
            public void onClick( DialogInterface dialog, int which )
            {
                dialog.dismiss( );
            }
        } );
        builder.setTitle( "Process selected text" );
        builder.setCancelable( false );
        builder.setView( layout );
        AlertDialog dlg = builder.create( );
        dlg.show( );
    }
    
    
    
    public void actualizarTextoLecturas(ArrayList<String> texto)
    {
        TextView text = (TextView) findViewById(R.id.txtVerb);
        if( texto != null )
        {
            for( int i = 0; i < texto.size( ); i++ )
            {
                text.clearComposingText( );
                text.setText( texto.get( i ) );
                System.out.println(texto.get( i ) );
            }
        }
        else
        {
            text.clearComposingText( );
            text.setText( "Cargando..." );
        }
    }


	
}
