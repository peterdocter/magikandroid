package com.radaee.reader;

import java.util.ArrayList;

import net.sf.andpdf.pdfviewer.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.contolers.magik.data.ControlerData;
import com.data.bd.PersistenceManager;
import com.example.magik.monitoring.DatosDisplay;
import com.radaee.grid.PDFGridItem;
import com.radaee.grid.PDFGridView;
import com.radaee.pdf.Document;
import com.radaee.pdf.Global;
import com.radaee.pdf.Page;
import com.radaee.reader.PDFReader.PDFReaderListener;
import com.radaee.view.PDFVPage;
import com.radaee.view.PDFViewThumb.PDFThumbListener;
import com.recomendacion.servicio.Servicio;

public class PDFReaderAct extends Activity implements OnItemClickListener, OnClickListener, PDFReaderListener, PDFThumbListener
{
    private PDFGridView m_vFiles = null;
    private PDFReader m_reader = null;
    private PDFThumbView m_thumb = null;
    private RelativeLayout m_layout;
    private Document m_doc = new Document( );
    private Button btn_ink;
    private Button btn_rect;
    private Button btn_oval;
    private Button btn_note;
    private Button btn_line;
    private Button btn_cancel;
    private Button btn_save;
    private Button btn_close;

    private Button btn_sel;
    private Button btn_act;
    private Button btn_edit;
    private Button btn_remove;

    private Button btn_prev;
    private Button btn_next;
    private EditText txt_find;
    private String str_find;
    private boolean m_set = false;
    private PDFVPage m_annot_vpage;
    private int m_annot;

    private Thread thread;
    private Thread recommendationsThread;

    private String CLASE_DYSPLAY = "";
    private ControlerData data;
    private DatosDisplay datosDisplay;
    
    private boolean sensorProcess;
    private PersistenceManager persistenceManager;

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        
        data = new ControlerData( );
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
                        if( datosDisplay.getLecturas( ).size( ) > 5 )
                        {
                            recomendaciones( );
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
        LinearLayout bar_act = ( LinearLayout )m_layout.findViewById( R.id.bar_act );
        LinearLayout bar_find = ( LinearLayout )m_layout.findViewById( R.id.bar_find );
        btn_ink = ( Button )bar_cmd.findViewById( R.id.btn_ink );
        btn_rect = ( Button )bar_cmd.findViewById( R.id.btn_rect );
        btn_oval = ( Button )bar_cmd.findViewById( R.id.btn_oval );
        btn_note = ( Button )bar_cmd.findViewById( R.id.btn_note );
        btn_line = ( Button )bar_cmd.findViewById( R.id.btn_line );
        btn_cancel = ( Button )bar_cmd.findViewById( R.id.btn_cancel );
        btn_save = ( Button )bar_cmd.findViewById( R.id.btn_save );
        btn_close = ( Button )bar_cmd.findViewById( R.id.btn_close );

        btn_sel = ( Button )bar_act.findViewById( R.id.btn_sel );
        btn_act = ( Button )bar_act.findViewById( R.id.btn_act );
        btn_edit = ( Button )bar_act.findViewById( R.id.btn_edit );
        btn_remove = ( Button )bar_act.findViewById( R.id.btn_remove );

        txt_find = ( EditText )bar_find.findViewById( R.id.txt_find );
        btn_prev = ( Button )bar_find.findViewById( R.id.btn_prev );
        btn_next = ( Button )bar_find.findViewById( R.id.btn_next );

        btn_sel.setOnClickListener( this );
        btn_act.setOnClickListener( this );
        btn_edit.setOnClickListener( this );
        btn_remove.setOnClickListener( this );

        btn_ink.setOnClickListener( this );
        btn_rect.setOnClickListener( this );
        btn_oval.setOnClickListener( this );
        btn_note.setOnClickListener( this );
        btn_line.setOnClickListener( this );
        btn_cancel.setOnClickListener( this );
        btn_save.setOnClickListener( this );
        btn_close.setOnClickListener( this );
        btn_prev.setOnClickListener( this );
        btn_next.setOnClickListener( this );
        btn_act.setEnabled( false );
        btn_save.setEnabled( false );
        btn_edit.setEnabled( false );
        btn_remove.setEnabled( false );
        btn_cancel.setEnabled( false );
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

    
    protected void recomendaciones( )
    {
        Servicio servicio = new Servicio( );
        String[] recs = servicio.getRecomendaciones( CLASE_DYSPLAY );
        persistenceManager = new PersistenceManager( this.getApplicationContext( ) );
        persistenceManager.saveRecommendations( CLASE_DYSPLAY, recs );
        persistenceManager = null;
        System.gc( );
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
                m_vFiles.PDFGotoSubdir( item.get_name( ) );
            }
            else
            {
                m_doc.Close( );
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
                btn_ink.setEnabled( m_reader.PDFCanSave( ) );
                btn_rect.setEnabled( m_reader.PDFCanSave( ) );
                btn_oval.setEnabled( m_reader.PDFCanSave( ) );
                btn_note.setEnabled( m_reader.PDFCanSave( ) );
                btn_save.setEnabled( m_reader.PDFCanSave( ) );
                
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
        btn_ink.setEnabled( !m_set );
        btn_rect.setEnabled( !m_set );
        btn_oval.setEnabled( !m_set );
        btn_note.setEnabled( !m_set );
        btn_line.setEnabled( !m_set );
        btn_cancel.setEnabled( false );

        btn_sel.setPressed( m_set );
        btn_act.setEnabled( false );
        btn_edit.setEnabled( false );
        btn_remove.setEnabled( false );
    }

    private void onInk( )
    {
        m_set = !m_set;
        if( m_set )
            m_reader.PDFSetInk( 0 );
        else
            m_reader.PDFSetInk( 1 );
        btn_ink.setPressed( m_set );
        btn_rect.setEnabled( !m_set );
        btn_oval.setEnabled( !m_set );
        btn_note.setEnabled( !m_set );
        btn_line.setEnabled( !m_set );
        btn_cancel.setEnabled( m_set );
        btn_save.setEnabled( !m_set );

        btn_sel.setEnabled( !m_set );
        btn_act.setEnabled( !m_set );
        btn_edit.setEnabled( !m_set );
        btn_remove.setEnabled( !m_set );

        btn_prev.setEnabled( !m_set );
        btn_next.setEnabled( !m_set );
        txt_find.setEnabled( !m_set );
    }

    private void onRect( )
    {
        m_set = !m_set;
        if( m_set )
            m_reader.PDFSetRect( 0 );
        else
            m_reader.PDFSetRect( 1 );
        btn_ink.setEnabled( !m_set );
        btn_rect.setPressed( m_set );
        btn_oval.setEnabled( !m_set );
        btn_note.setEnabled( !m_set );
        btn_line.setEnabled( !m_set );
        btn_cancel.setEnabled( m_set );
        btn_save.setEnabled( !m_set );

        btn_sel.setEnabled( !m_set );
        btn_act.setEnabled( !m_set );
        btn_edit.setEnabled( !m_set );
        btn_remove.setEnabled( !m_set );

        btn_prev.setEnabled( !m_set );
        btn_next.setEnabled( !m_set );
        txt_find.setEnabled( !m_set );
    }

    private void onOval( )
    {
        m_set = !m_set;
        if( m_set )
            m_reader.PDFSetEllipse( 0 );
        else
            m_reader.PDFSetEllipse( 1 );
        btn_ink.setEnabled( !m_set );
        btn_rect.setEnabled( !m_set );
        btn_oval.setPressed( m_set );
        btn_note.setEnabled( !m_set );
        btn_line.setEnabled( !m_set );
        btn_cancel.setEnabled( m_set );
        btn_save.setEnabled( !m_set );

        btn_sel.setEnabled( !m_set );
        btn_act.setEnabled( !m_set );
        btn_edit.setEnabled( !m_set );
        btn_remove.setEnabled( !m_set );

        btn_prev.setEnabled( !m_set );
        btn_next.setEnabled( !m_set );
        txt_find.setEnabled( !m_set );
    }

    private void onNote( )
    {
        m_reader.PDFSetNote( );
        m_set = !m_set;
        btn_ink.setEnabled( !m_set );
        btn_rect.setEnabled( !m_set );
        btn_oval.setEnabled( !m_set );
        btn_note.setPressed( m_set );
        btn_line.setEnabled( !m_set );
        btn_cancel.setEnabled( false );

        btn_sel.setEnabled( !m_set );
        btn_act.setEnabled( !m_set );
        btn_edit.setEnabled( !m_set );
        btn_remove.setEnabled( !m_set );
    }

    private void onLine( )
    {
        m_set = !m_set;
        if( m_set )
            m_reader.PDFSetLine( 0 );
        else
            m_reader.PDFSetLine( 1 );
        btn_ink.setEnabled( !m_set );
        btn_rect.setEnabled( !m_set );
        btn_oval.setEnabled( !m_set );
        btn_note.setEnabled( !m_set );
        btn_line.setPressed( m_set );
        btn_cancel.setEnabled( false );

        btn_sel.setEnabled( !m_set );
        btn_act.setEnabled( !m_set );
        btn_edit.setEnabled( !m_set );
        btn_remove.setEnabled( !m_set );
    }

    private void onCancel( )
    {
        m_reader.PDFCancel( );
        m_set = false;
        btn_ink.setEnabled( true );
        btn_rect.setEnabled( true );
        btn_oval.setEnabled( true );
        btn_note.setEnabled( true );
        btn_line.setEnabled( true );
        btn_cancel.setEnabled( false );
        btn_save.setEnabled( true );

        btn_sel.setEnabled( true );
        btn_act.setEnabled( true );
        btn_edit.setEnabled( true );
        btn_remove.setEnabled( true );

        btn_prev.setEnabled( true );
        btn_next.setEnabled( true );
        txt_find.setEnabled( true );
    }

    private void onFindPrev( )
    {
        String str = txt_find.getText( ).toString( );
        if( str_find != null )
        {
            if( str != null && str.compareTo( str_find ) == 0 )
            {
                m_reader.PDFFind( -1 );
                return;
            }
        }
        if( str != null && str.length( ) > 0 )
        {
            m_reader.PDFFindStart( str, false, false );
            m_reader.PDFFind( 1 );
            str_find = str;
        }
    }

    private void onFindNext( )
    {
        String str = txt_find.getText( ).toString( );
        if( str_find != null )
        {
            if( str != null && str.compareTo( str_find ) == 0 )
            {
                m_reader.PDFFind( 1 );
                return;
            }
        }
        if( str != null && str.length( ) > 0 )
        {
            m_reader.PDFFindStart( str, false, false );
            m_reader.PDFFind( 1 );
            str_find = str;
        }
    }

    private void onEdit( )
    {
        LinearLayout layout = ( LinearLayout )LayoutInflater.from( this ).inflate( R.layout.dlg_note, null );
        final EditText subj = ( EditText )layout.findViewById( R.id.txt_subj );
        final EditText content = ( EditText )layout.findViewById( R.id.txt_content );
        final Page page = m_annot_vpage.GetPage( );
        if( page == null )
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setPositiveButton( "OK", new DialogInterface.OnClickListener( )
        {
            public void onClick( DialogInterface dialog, int which )
            {
                String str_subj = subj.getText( ).toString( );
                String str_content = content.getText( ).toString( );
                page.SetAnnotPopupSubject( m_annot, str_subj );
                page.SetAnnotPopupText( m_annot, str_content );
                dialog.dismiss( );
                m_reader.PDFEndAnnot( );
                m_set = false;
                btn_ink.setEnabled( true );
                btn_rect.setEnabled( true );
                btn_oval.setEnabled( true );
                btn_note.setEnabled( true );
                btn_cancel.setEnabled( false );
                btn_save.setEnabled( true );

                btn_sel.setEnabled( true );
                btn_act.setEnabled( false );
                btn_edit.setEnabled( false );
                btn_remove.setEnabled( false );

                btn_prev.setEnabled( true );
                btn_next.setEnabled( true );
                txt_find.setEnabled( true );
            }
        } );
        builder.setNegativeButton( "Cancel", new DialogInterface.OnClickListener( )
        {
            public void onClick( DialogInterface dialog, int which )
            {
                dialog.dismiss( );
                m_reader.PDFEndAnnot( );
                m_set = false;
                btn_ink.setEnabled( true );
                btn_rect.setEnabled( true );
                btn_oval.setEnabled( true );
                btn_note.setEnabled( true );
                btn_cancel.setEnabled( false );
                btn_save.setEnabled( true );

                btn_sel.setEnabled( true );
                btn_act.setEnabled( false );
                btn_edit.setEnabled( false );
                btn_remove.setEnabled( false );

                btn_prev.setEnabled( true );
                btn_next.setEnabled( true );
                txt_find.setEnabled( true );
            }
        } );
        builder.setTitle( "Note Content" );
        builder.setCancelable( false );
        builder.setView( layout );

        subj.setText( page.GetAnnotPopupSubject( m_annot ) );
        content.setText( page.GetAnnotPopupText( m_annot ) );
        AlertDialog dlg = builder.create( );
        dlg.show( );
    }

    private void onAct( )
    {
        m_reader.PDFPerformAnnot( );
        m_set = false;
        btn_ink.setEnabled( true );
        btn_rect.setEnabled( true );
        btn_oval.setEnabled( true );
        btn_note.setEnabled( true );
        btn_line.setEnabled( true );
        btn_cancel.setEnabled( false );
        btn_save.setEnabled( true );

        btn_sel.setEnabled( true );
        btn_act.setEnabled( false );
        btn_edit.setEnabled( false );
        btn_remove.setEnabled( false );

        btn_prev.setEnabled( true );
        btn_next.setEnabled( true );
        txt_find.setEnabled( true );
    }

    private void onRemove( )
    {
        m_reader.PDFRemoveAnnot( );
        m_set = false;
        btn_ink.setEnabled( true );
        btn_rect.setEnabled( true );
        btn_oval.setEnabled( true );
        btn_note.setEnabled( true );
        btn_line.setEnabled( true );
        btn_cancel.setEnabled( false );
        btn_save.setEnabled( true );

        btn_sel.setEnabled( true );
        btn_act.setEnabled( false );
        btn_edit.setEnabled( false );
        btn_remove.setEnabled( false );

        btn_prev.setEnabled( true );
        btn_next.setEnabled( true );
        txt_find.setEnabled( true );
    }

    public void onClick( View v )
    {
        switch( v.getId( ) )
        {
            case R.id.btn_ink:
                onInk( );
                break;
            case R.id.btn_rect:
                onRect( );
                break;
            case R.id.btn_oval:
                onOval( );
                break;
            case R.id.btn_note:
                onNote( );
                break;
            case R.id.btn_line:
                onLine( );
                break;
            case R.id.btn_cancel:
                onCancel( );
                break;
            case R.id.btn_save:
                m_reader.PDFSave( );
                break;
            case R.id.btn_sel:
                onSelect( );
                break;
            case R.id.btn_remove:
                onRemove( );
                break;
            case R.id.btn_act:
                onAct( );
                break;
            case R.id.btn_edit:
                onEdit( );
                break;
            case R.id.btn_prev:
                onFindPrev( );
                break;
            case R.id.btn_next:
                onFindNext( );
                break;
            case R.id.btn_close:
                m_thumb.thumbClose( );
                m_reader.PDFClose( );
                if( m_doc != null )
                    m_doc.Close( );
                str_find = null;
                setContentView( m_vFiles );
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
        m_annot_vpage = vpage;
        m_annot = annot;
        btn_ink.setEnabled( annot == 0 );
        btn_rect.setEnabled( annot == 0 );
        btn_oval.setEnabled( annot == 0 );
        btn_note.setEnabled( annot == 0 );
        btn_cancel.setEnabled( false );
        btn_save.setEnabled( annot == 0 );

        btn_sel.setEnabled( annot == 0 );
        btn_act.setEnabled( annot != 0 );
        btn_edit.setEnabled( annot != 0 );
        btn_remove.setEnabled( annot != 0 );

        btn_prev.setEnabled( annot == 0 );
        btn_next.setEnabled( annot == 0 );
        txt_find.setEnabled( annot == 0 );
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

//    public boolean onTouchEvent( MotionEvent event )
//    {
//        String captura = "";
//        captura = display.velocityTrack( event );
//        float valorx = 0;
//        float valory = 0;
//        if( captura != "" && captura != null )
//        {
//            String partexy[] = captura.split( ":" );
//            String valores[] = partexy[ 1 ].split( ";" );
//            valorx = Float.parseFloat( valores[ 0 ] );
//            valory = Float.parseFloat( valores[ 1 ] );
//        }
//        int a = ( int )valorx;
//        int b = ( int )valory;
//
//        String rtaX = display.analizarVelocidad( a, "X" );
//        String rtaY = display.analizarVelocidad( b, "Y" );
//
//        if( captura != "" )
//        {
//            rta += "\n";
//            rta += captura + ";" + rtaX + ";" + rtaY + ";";
//            if( rta.contains( "LECTURA" ) )
//            {
//                lecturas.add( rta );
//            }
//
//        }
//        return false;
//    }
}
