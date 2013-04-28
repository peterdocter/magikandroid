package com.radaee.reader;

import net.sf.andpdf.pdfviewer.R;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.magik.monitoring.ControlerDisplay;
import com.example.magik.monitoring.DatosDisplay;
import com.radaee.pdf.Document;
import com.radaee.pdf.Global;
import com.radaee.pdf.Ink;
import com.radaee.pdf.Matrix;
import com.radaee.pdf.Page;
import com.radaee.view.PDFVPage;
import com.radaee.view.PDFView;
import com.radaee.view.PDFView.PDFPos;
import com.radaee.view.PDFView.PDFViewListener;
import com.radaee.view.PDFViewCurl;
import com.radaee.view.PDFViewDual;
import com.radaee.view.PDFViewHorz;
import com.radaee.view.PDFViewVert;

public class PDFReader extends View implements PDFViewListener, OnItemClickListener, PopupWindow.OnDismissListener
{
    private DatosDisplay datos;
    private ControlerDisplay display;
    
    private PDFView m_view = null;
    private Document m_doc = null;
    private PopupWindow m_pEdit = null;
    private PopupWindow m_pCombo = null;
    static final int STA_NORMAL = 0;
    static final int STA_SELECT = 1;
    static final int STA_INK = 2;
    static final int STA_RECT = 3;
    static final int STA_ELLIPSE = 4;
    static final int STA_NOTE = 5;
    static final int STA_LINE = 6;
    static final int STA_ANNOT = 100;
    private int m_status = STA_NORMAL;
    private int m_annot = 0;
    private PDFPos m_annot_pos = null;
    private PDFVPage m_annot_page = null;
    private float m_annot_rect[];
    private Ink m_ink;
    private PDFVPage m_ink_page;
    private float m_rects[];
    private PDFReaderListener m_listener;
    private int m_pageno = 0;
    private int m_edit_type = 0;
    private int m_sel_index = -1;
    public interface PDFReaderListener
    {
        public void OnPageChanged( int pageno );
        public void OnAnnotClicked( PDFVPage vpage, int annot );
        public void OnSelectEnd( String text );
        public void OnOpenURI( String uri );
        public void OnOpenMovie( String path );
        public void OnOpenSound( int[] paras, String path );
        public void OnOpenAttachment( String path );
        public void OnOpen3D( String path );
    }
    public PDFReader( Context context, AttributeSet attrs )
    {
        super( context, attrs );
        display = new ControlerDisplay( );
        
        this.setBackgroundColor( 0xFFCCCCCC );
        m_pEdit = new PopupWindow( LayoutInflater.from( context ).inflate( R.layout.pop_edit, null ) );
        m_pCombo = new PopupWindow( LayoutInflater.from( context ).inflate( R.layout.pop_combo, null ) );
        m_pEdit.setOnDismissListener( this );
        m_pCombo.setOnDismissListener( this );
        m_pEdit.setFocusable( true );
        m_pEdit.setTouchable( true );
        BitmapDrawable bitmap = new BitmapDrawable( );// add back
        m_pEdit.setBackgroundDrawable( bitmap );
        m_pCombo.setFocusable( true );
        m_pCombo.setTouchable( true );
        m_pCombo.setBackgroundDrawable( bitmap );
    }
    public void PDFOpen( Document doc, PDFReaderListener listener )
    {
        PDFClose( );
        m_listener = listener;
        m_doc = doc;
        int back_color = 0xFFCCCCCC;
        switch( Global.def_view )
        {
            case 1:
                m_view = new PDFViewHorz( this.getContext( ) );
                break;
            case 2:
                m_view = new PDFViewCurl( this.getContext( ) );
                back_color = 0xFFFFFFFF;
                break;
            case 3:
            {
                PDFViewDual view = new PDFViewDual( this.getContext( ) );
                boolean paras[] = new boolean[m_doc.GetPageCount( )];
                int cur = 0;
                while( cur < paras.length )
                {
                    paras[ cur ] = false;
                    cur++;
                }
                view.vSetLayoutPara( null, paras );
                m_view = view;
            }
                break;
            case 4:
            case 6:
            {
                PDFViewDual view = new PDFViewDual( this.getContext( ) );
                view.vSetLayoutPara( null, null );
                m_view = view;
            }
                break;
            default:
                m_view = new PDFViewVert( this.getContext( ) );
                break;
        }
        m_view.vOpen( m_doc, 4, back_color, this );
        m_view.vResize( getWidth( ), getHeight( ) );
    }
    public boolean PDFSave( )
    {
        return m_doc.Save( );
    }
    public void PDFClose( )
    {
        PDFCancel( );
        if( m_ink != null )
        {
            m_ink.Destroy( );
            m_ink = null;
        }
        if( m_view != null )
        {
            m_view.vClose( );
            m_view = null;
        }
        if( m_doc != null )
            m_doc = null;
        m_status = STA_NORMAL;
        m_annot = 0;
        m_annot_pos = null;
        m_annot_page = null;
        m_annot_rect = null;
        m_ink_page = null;
        m_rects = null;
        m_pageno = 0;
    }
    public boolean PDFCanSave( )
    {
        if( m_doc == null )
            return false;
        return m_doc.CanSave( );
    }
    public void OnPDFPosChanged( PDFPos pos )
    {
        if( m_listener != null )
        {
            PDFVPage vpage = m_view.vGetPage( pos.pageno );
            int left = vpage.GetVX( m_view.vGetX( ) );
            int top = vpage.GetVY( m_view.vGetY( ) );
            int right = left + vpage.GetWidth( );
            int bottom = top + vpage.GetHeight( );
            int pageno = m_pageno;
            if( m_view.getClass( ).equals( PDFViewVert.class ) )
            {
                if( bottom < m_view.vGetWinW( ) / 4 )
                    pageno = pos.pageno + 1;
                else
                    pageno = pos.pageno;
            }
            else
            {
                if( right < m_view.vGetWinW( ) / 4 )
                    pageno = pos.pageno + 1;
                else
                    pageno = pos.pageno;
            }
            if( pageno != m_pageno )
            {
                m_pageno = pageno;
                m_listener.OnPageChanged( pageno );
            }
        }
    }
    public boolean OnPDFDoubleTapped( float x, float y )
    {
        if( m_status != STA_NORMAL )
            return false;
        m_view.vSetScale( m_view.vGetScale( ) + Global.zoomStep, x, y );
        return true;
    }
    public boolean OnPDFSingleTapped( float x, float y )
    {
        if( m_status == STA_NORMAL || m_status == STA_ANNOT )
        {
            m_annot_pos = m_view.vGetPos( ( int )x, ( int )y );
            m_annot_page = m_view.vGetPage( m_annot_pos.pageno );
            Page page = m_annot_page.GetPage( );
            if( page == null )
                m_annot = 0;
            else
                m_annot = page.GetAnnotFromPoint( m_annot_pos.x, m_annot_pos.y );
            if( m_annot == 0 )
            {
                m_annot_page = null;
                m_annot_pos = null;
                m_annot_rect = null;
                m_view.vSetLock( 0 );
                if( m_listener != null && m_status == STA_ANNOT )
                    m_listener.OnAnnotClicked( m_annot_page, 0 );
                m_status = STA_NORMAL;
            }
            else
            {
                m_annot_rect = new float[4];
                page.GetAnnotRect( m_annot, m_annot_rect );
                int px = m_annot_page.GetVX( m_view.vGetX( ) );
                int py = m_annot_page.GetVY( m_view.vGetY( ) );
                float tmp = m_annot_rect[ 1 ];
                m_annot_rect[ 0 ] = m_annot_page.ToDIBX( m_annot_rect[ 0 ] ) + px;
                m_annot_rect[ 1 ] = m_annot_page.ToDIBY( m_annot_rect[ 3 ] ) + py;
                m_annot_rect[ 2 ] = m_annot_page.ToDIBX( m_annot_rect[ 2 ] ) + px;
                m_annot_rect[ 3 ] = m_annot_page.ToDIBY( tmp ) + py;
                m_view.vSetLock( 3 );
                m_status = STA_ANNOT;
                if( m_listener != null )
                    m_listener.OnAnnotClicked( m_annot_page, m_annot );
                if( m_doc.CanSave( ) && page.GetAnnotEditType( m_annot ) > 0 )
                {
                    int[] location = new int[2];
                    getLocationOnScreen( location );
                    m_pEdit.setWidth( ( int ) ( m_annot_rect[ 2 ] - m_annot_rect[ 0 ] ) );
                    m_pEdit.setHeight( ( int ) ( m_annot_rect[ 3 ] - m_annot_rect[ 1 ] ) );
                    EditText edit = ( EditText )m_pEdit.getContentView( ).findViewById( R.id.annot_text );
                    edit.setBackgroundColor( 0xFFFFFFC0 );
                    edit.setTextSize( page.GetAnnotEditTextSize( m_annot ) * m_view.vGetScale( ) );
                    edit.setPadding( 2, 2, 2, 2 );
                    switch( page.GetAnnotEditType( m_annot ) )
                    {
                        case 1:
                            edit.setSingleLine( );
                            edit.setInputType( InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_NORMAL );
                            break;
                        case 2:
                            edit.setSingleLine( );
                            edit.setInputType( InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_PASSWORD );
                            break;
                        case 3:
                            edit.setSingleLine( false );
                            edit.setInputType( InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_NORMAL );
                            break;
                    }
                    edit.setText( page.GetAnnotEditText( m_annot ) );
                    m_edit_type = 1;
                    m_pEdit.showAtLocation( this, Gravity.NO_GRAVITY, ( int )m_annot_rect[ 0 ] + location[ 0 ], ( int )m_annot_rect[ 1 ] + location[ 1 ] );
                }
                if( m_doc.CanSave( ) && page.GetAnnotComboItemCount( m_annot ) >= 0 )
                {
                    int[] location = new int[2];
                    getLocationOnScreen( location );
                    String opts[] = new String[page.GetAnnotComboItemCount( m_annot )];
                    int cur = 0;
                    while( cur < opts.length )
                    {
                        opts[ cur ] = page.GetAnnotComboItem( m_annot, cur );
                        cur++;
                    }
                    m_pCombo.setWidth( ( int ) ( m_annot_rect[ 2 ] - m_annot_rect[ 0 ] ) );
                    if( ( m_annot_rect[ 3 ] - m_annot_rect[ 1 ] - 4 ) * opts.length > 250 )
                        m_pCombo.setHeight( 250 );
                    else
                        m_pCombo.setHeight( ( int ) ( m_annot_rect[ 3 ] - m_annot_rect[ 1 ] - 4 ) * opts.length );
                    ComboList combo = ( ComboList )m_pCombo.getContentView( ).findViewById( R.id.annot_combo );
                    combo.set_opts( opts );
                    combo.setOnItemClickListener( this );
                    m_edit_type = 2;
                    m_sel_index = -1;
                    m_pCombo.showAtLocation( this, Gravity.NO_GRAVITY, ( int )m_annot_rect[ 0 ] + location[ 0 ], ( int ) ( m_annot_rect[ 3 ] + location[ 1 ] ) );
                }
                int check = page.GetAnnotCheckStatus( m_annot );
                if( m_doc.CanSave( ) && check >= 0 )
                {
                    switch( check )
                    {
                        case 0:
                            page.SetAnnotCheckValue( m_annot, true );
                            break;
                        case 1:
                            page.SetAnnotCheckValue( m_annot, false );
                            break;
                        case 2:
                        case 3:
                            page.SetAnnotRadio( m_annot );
                            break;
                    }
                    m_view.vRender( m_annot_page );
                    PDFEndAnnot( );
                }
                invalidate( );
            }
            return true;
        }
        return false;
    }
    public void OnPDFLongPressed( float x, float y )
    {
    }
    public void OnPDFShowPressed( float x, float y )
    {
    }
    public void OnPDFSelectEnd( )
    {
        if( m_listener != null )
            m_listener.OnSelectEnd( m_view.vGetSel( ) );
    }
    public void OnPDFFound( boolean found )
    {
        if( !found )
            Toast.makeText( getContext( ), "no more found", Toast.LENGTH_SHORT ).show( );
    }
    public void PDFSetInk( int code )
    {
        if( code == 0 )// start
        {
            m_status = STA_INK;
            m_ink = new Ink( Global.inkWidth );
            m_view.vSetLock( 3 );
        }
        else if( code == 1 )// end
        {
            m_status = STA_NORMAL;
            if( m_ink_page != null )
            {
                Page page = m_ink_page.GetPage( );
                if( page != null )
                {
                    Matrix mat = m_ink_page.CreateMatrix( );
                    page.AddAnnotInk( mat, m_ink, -m_ink_page.GetVX( m_view.vGetX( ) ), -m_ink_page.GetVY( m_view.vGetY( ) ) );
                    mat.Destroy( );
                    m_view.vRender( m_ink_page );
                }
            }
            m_ink.Destroy( );
            m_ink = null;
            m_ink_page = null;
            invalidate( );
            m_view.vSetLock( 0 );
        }
        else
        // cancel
        {
            m_status = STA_NORMAL;
            m_ink.Destroy( );
            m_ink = null;
            m_ink_page = null;
            invalidate( );
            m_view.vSetLock( 0 );
        }
    }
    public void PDFSetRect( int code )
    {
        if( code == 0 )// start
        {
            m_status = STA_RECT;
            m_view.vSetLock( 3 );
        }
        else if( code == 1 )// end
        {
            if( m_rects != null )
            {
                int len = m_rects.length;
                int cur;
                for( cur = 0; cur < len; cur += 4 )
                {
                    PDFPos pos = m_view.vGetPos( ( int )m_rects[ cur ], ( int )m_rects[ cur + 1 ] );
                    PDFVPage vpage = m_view.vGetPage( pos.pageno );
                    Page page = vpage.GetPage( );
                    if( page != null )
                    {
                        Matrix mat = vpage.CreateMatrix( );
                        float rect[] = new float[4];
                        if( m_rects[ cur ] > m_rects[ cur + 2 ] )
                        {
                            rect[ 0 ] = m_rects[ cur + 2 ] - vpage.GetVX( m_view.vGetX( ) );
                            rect[ 2 ] = m_rects[ cur ] - vpage.GetVX( m_view.vGetX( ) );
                        }
                        else
                        {
                            rect[ 0 ] = m_rects[ cur ] - vpage.GetVX( m_view.vGetX( ) );
                            rect[ 2 ] = m_rects[ cur + 2 ] - vpage.GetVX( m_view.vGetX( ) );
                        }
                        if( m_rects[ cur + 1 ] > m_rects[ cur + 3 ] )
                        {
                            rect[ 1 ] = m_rects[ cur + 3 ] - vpage.GetVY( m_view.vGetY( ) );
                            rect[ 3 ] = m_rects[ cur + 1 ] - vpage.GetVY( m_view.vGetY( ) );
                        }
                        else
                        {
                            rect[ 1 ] = m_rects[ cur + 1 ] - vpage.GetVY( m_view.vGetY( ) );
                            rect[ 3 ] = m_rects[ cur + 3 ] - vpage.GetVY( m_view.vGetY( ) );
                        }
                        page.AddAnnotRect( mat, rect, 3, 0x80FF0000, 0x800000FF );
                        mat.Destroy( );
                        m_view.vRender( vpage );
                    }
                }
            }
            m_status = STA_NORMAL;
            m_rects = null;
            invalidate( );
            m_view.vSetLock( 0 );
        }
        else
        // cancel
        {
            m_status = STA_NORMAL;
            m_rects = null;
            invalidate( );
            m_view.vSetLock( 0 );
        }
    }
    public void PDFSetEllipse( int code )
    {
        if( code == 0 )// start
        {
            m_status = STA_ELLIPSE;
            m_view.vSetLock( 3 );
        }
        else if( code == 1 )// end
        {
            if( m_rects != null )
            {
                int len = m_rects.length;
                int cur;
                for( cur = 0; cur < len; cur += 4 )
                {
                    PDFPos pos = m_view.vGetPos( ( int )m_rects[ cur ], ( int )m_rects[ cur + 1 ] );
                    PDFVPage vpage = m_view.vGetPage( pos.pageno );
                    Page page = vpage.GetPage( );
                    if( page != null )
                    {
                        Matrix mat = vpage.CreateMatrix( );
                        float rect[] = new float[4];
                        if( m_rects[ cur ] > m_rects[ cur + 2 ] )
                        {
                            rect[ 0 ] = m_rects[ cur + 2 ] - vpage.GetVX( m_view.vGetX( ) );
                            rect[ 2 ] = m_rects[ cur ] - vpage.GetVX( m_view.vGetX( ) );
                        }
                        else
                        {
                            rect[ 0 ] = m_rects[ cur ] - vpage.GetVX( m_view.vGetX( ) );
                            rect[ 2 ] = m_rects[ cur + 2 ] - vpage.GetVX( m_view.vGetX( ) );
                        }
                        if( m_rects[ cur + 1 ] > m_rects[ cur + 3 ] )
                        {
                            rect[ 1 ] = m_rects[ cur + 3 ] - vpage.GetVY( m_view.vGetY( ) );
                            rect[ 3 ] = m_rects[ cur + 1 ] - vpage.GetVY( m_view.vGetY( ) );
                        }
                        else
                        {
                            rect[ 1 ] = m_rects[ cur + 1 ] - vpage.GetVY( m_view.vGetY( ) );
                            rect[ 3 ] = m_rects[ cur + 3 ] - vpage.GetVY( m_view.vGetY( ) );
                        }
                        page.AddAnnotEllipse( mat, rect, 3, 0x80FF0000, 0x800000FF );
                        mat.Destroy( );
                        m_view.vRender( vpage );
                    }
                }
            }
            m_status = STA_NORMAL;
            m_rects = null;
            invalidate( );
            m_view.vSetLock( 0 );
        }
        else
        // cancel
        {
            m_status = STA_NORMAL;
            m_rects = null;
            invalidate( );
            m_view.vSetLock( 0 );
        }
    }
    public void PDFSetSelect( )
    {
        if( m_status == STA_SELECT )
        {
            m_status = STA_NORMAL;
            m_view.vSetSelStatus( false );
        }
        else
        {
            m_status = STA_SELECT;
            m_view.vSetSelStatus( true );
        }
    }
    public void PDFSetNote( )
    {
        if( m_status == STA_NOTE )
        {
            m_status = STA_NORMAL;
            m_view.vSetLock( 0 );
        }
        else
        {
            m_status = STA_NOTE;
            m_view.vSetLock( 3 );
        }
    }
    public void PDFSetLine( int code )
    {
        if( code == 0 )// start
        {
            m_status = STA_LINE;
            m_view.vSetLock( 3 );
        }
        else if( code == 1 )// end
        {
            if( m_rects != null )
            {
                int len = m_rects.length;
                int cur;
                float[] pt1 = new float[2];
                float[] pt2 = new float[2];
                for( cur = 0; cur < len; cur += 4 )
                {
                    PDFPos pos = m_view.vGetPos( ( int )m_rects[ cur ], ( int )m_rects[ cur + 1 ] );
                    PDFVPage vpage = m_view.vGetPage( pos.pageno );
                    pt1[ 0 ] = m_rects[ cur ] - vpage.GetX( );
                    pt1[ 1 ] = m_rects[ cur + 1 ] - vpage.GetY( );
                    pt2[ 0 ] = m_rects[ cur + 2 ] - vpage.GetX( );
                    pt2[ 1 ] = m_rects[ cur + 3 ] - vpage.GetY( );
                    Page page = vpage.GetPage( );
                    if( page != null )
                    {
                        Matrix mat = vpage.CreateMatrix( );
                        page.AddAnnotLine( mat, pt1, pt2, 1, 0, 3, 0x80FF0000, 0x800000FF );
                        mat.Destroy( );
                        m_view.vRender( vpage );
                    }
                }
            }
            m_status = STA_NORMAL;
            m_rects = null;
            invalidate( );
            m_view.vSetLock( 0 );
        }
        else
        // cancel
        {
            m_status = STA_NORMAL;
            m_rects = null;
            invalidate( );
            m_view.vSetLock( 0 );
        }
    }
    public void PDFCancel( )
    {
        if( m_status == STA_NOTE )
            PDFSetNote( );
        if( m_status == STA_RECT )
            PDFSetRect( 2 );
        if( m_status == STA_INK )
            PDFSetInk( 2 );
        if( m_status == STA_LINE )
            PDFSetLine( 2 );
        if( m_status == STA_ELLIPSE )
            PDFSetEllipse( 2 );
        if( m_status == STA_ANNOT )
            PDFEndAnnot( );
        invalidate( );
    }
    public void PDFRemoveAnnot( )
    {
        if( m_status != STA_ANNOT || !m_doc.CanSave( ) )
            return;
        Page page = m_annot_page.GetPage( );
        page.RemoveAnnot( m_annot );
        m_view.vRender( m_annot_page );
        PDFEndAnnot( );
    }
    public void PDFEndAnnot( )
    {
        if( m_status != STA_ANNOT )
            return;
        m_annot_page = null;
        m_annot_pos = null;
        m_annot = 0;
        m_view.vSetLock( 0 );
        invalidate( );
        m_status = STA_NORMAL;
        if( m_listener != null )
            m_listener.OnAnnotClicked( null, 0 );
    }
    public void PDFPerformAnnot( )
    {
        if( m_status != STA_ANNOT )
            return;
        Page page = m_annot_page.GetPage( );
        if( page == null )
            return;
        int dest = page.GetAnnotDest( m_annot );
        if( dest >= 0 )
            m_view.vGotoPage( dest );
        String uri = page.GetAnnotURI( m_annot );
        if( m_listener != null )
            m_listener.OnOpenURI( uri );
        int index;
        String mov = page.GetAnnotMovie( m_annot );
        if( mov != null )
        {
            index = -1;
            if( index < 0 )
                index = mov.lastIndexOf( '\\' );
            if( index < 0 )
                index = mov.lastIndexOf( '/' );
            if( index < 0 )
                index = mov.lastIndexOf( ':' );
            String save_file = Global.tmp_path + "/" + mov.substring( index + 1 );
            page.GetAnnotMovieData( m_annot, save_file );
            if( m_listener != null )
                m_listener.OnOpenMovie( save_file );
        }
        String snd = page.GetAnnotSound( m_annot );
        if( snd != null )
        {
            int paras[] = new int[4];
            index = -1;
            if( index < 0 )
                index = snd.lastIndexOf( '\\' );
            if( index < 0 )
                index = snd.lastIndexOf( '/' );
            if( index < 0 )
                index = snd.lastIndexOf( ':' );
            String save_file = Global.tmp_path + "/" + snd.substring( index + 1 );
            page.GetAnnotSoundData( m_annot, paras, save_file );
            if( m_listener != null )
                m_listener.OnOpenSound( paras, save_file );
        }
        String att = page.GetAnnotAttachment( m_annot );
        if( att != null )
        {
            index = -1;
            if( index < 0 )
                index = att.lastIndexOf( '\\' );
            if( index < 0 )
                index = att.lastIndexOf( '/' );
            if( index < 0 )
                index = att.lastIndexOf( ':' );
            String save_file = Global.tmp_path + "/" + att.substring( index + 1 );
            page.GetAnnotAttachmentData( m_annot, save_file );
            if( m_listener != null )
                m_listener.OnOpenAttachment( save_file );
        }
        String f3d = page.GetAnnot3D( m_annot );
        if( f3d != null )
        {
            index = -1;
            if( index < 0 )
                index = f3d.lastIndexOf( '\\' );
            if( index < 0 )
                index = f3d.lastIndexOf( '/' );
            if( index < 0 )
                index = f3d.lastIndexOf( ':' );
            String save_file = Global.tmp_path + "/" + f3d.substring( index + 1 );
            page.GetAnnot3DData( m_annot, save_file );
            if( m_listener != null )
                m_listener.OnOpen3D( save_file );
        }

        boolean reset = page.GetAnnotReset( m_annot );
        if( reset && m_doc.CanSave( ) )
        {
            page.SetAnnotReset( m_annot );
            m_view.vRender( m_annot_page );
        }
        String tar = page.GetAnnotSubmitTarget( m_annot );
        if( tar != null )
        {
            if( m_listener != null )
                m_listener.OnOpenURI( tar + "?" + page.GetAnnotSubmitPara( m_annot ) );
        }
        PDFEndAnnot( );
    }
    public void PDFFindStart( String key, boolean match_case, boolean whole_word )
    {
        m_view.vFindStart( key, match_case, whole_word );
    }
    public void PDFFind( int dir )
    {
        m_view.vFind( dir );
    }
    public boolean PDFSetSelMarkup( int type )
    {
        return m_view.vSetSelMarkup( type );
    }
    public void OnPDFInvalidate( boolean post )
    {
        if( post )
            postInvalidate( );
        else
            invalidate( );
    }
    public void onItemClick( AdapterView<?> arg0, View arg1, int arg2, long arg3 )
    {
        m_sel_index = arg2;
        m_pCombo.dismiss( );
    }
    public void onDismiss( )
    {
        Page page = m_annot_page.GetPage( );
        if( m_edit_type == 1 )// edit box
        {
            EditText edit = ( EditText )m_pEdit.getContentView( ).findViewById( R.id.annot_text );
            page.SetAnnotEditText( m_annot, edit.getText( ).toString( ) );
            m_view.vRender( m_annot_page );
            PDFEndAnnot( );
        }
        if( m_edit_type == 2 )// combo
        {
            if( m_sel_index >= 0 )
            {
                page.SetAnnotComboItem( m_annot, m_sel_index );
                m_view.vRender( m_annot_page );
            }
            m_sel_index = -1;
            PDFEndAnnot( );
        }
        m_edit_type = 0;
    }
    private void onDrawAnnot( Canvas canvas )
    {
        if( m_status == STA_ANNOT )
        {
            Paint paint = new Paint( );
            paint.setStyle( Style.STROKE );
            paint.setStrokeWidth( 2 );
            paint.setARGB( 0x80, 0, 0, 0 );
            canvas.drawRect( m_annot_rect[ 0 ], m_annot_rect[ 1 ], m_annot_rect[ 2 ], m_annot_rect[ 3 ], paint );
        }
    }
    private void onDrawRect( Canvas canvas )
    {
        if( m_status == STA_RECT && m_rects != null )
        {
            int len = m_rects.length;
            int cur;
            Paint paint1 = new Paint( );
            Paint paint2 = new Paint( );
            paint1.setStyle( Style.STROKE );
            paint1.setStrokeWidth( 3 );
            paint1.setARGB( 0x80, 0xFF, 0, 0 );
            paint2.setStyle( Style.FILL );
            paint2.setARGB( 0x80, 0, 0, 0xFF );
            for( cur = 0; cur < len; cur += 4 )
            {
                float rect[] = new float[4];
                if( m_rects[ cur ] > m_rects[ cur + 2 ] )
                {
                    rect[ 0 ] = m_rects[ cur + 2 ];
                    rect[ 2 ] = m_rects[ cur ];
                }
                else
                {
                    rect[ 0 ] = m_rects[ cur ];
                    rect[ 2 ] = m_rects[ cur + 2 ];
                }
                if( m_rects[ cur + 1 ] > m_rects[ cur + 3 ] )
                {
                    rect[ 1 ] = m_rects[ cur + 3 ];
                    rect[ 3 ] = m_rects[ cur + 1 ];
                }
                else
                {
                    rect[ 1 ] = m_rects[ cur + 1 ];
                    rect[ 3 ] = m_rects[ cur + 3 ];
                }
                canvas.drawRect( rect[ 0 ], rect[ 1 ], rect[ 2 ], rect[ 3 ], paint1 );
                canvas.drawRect( rect[ 0 ] + 1.5f, rect[ 1 ] + 1.5f, rect[ 2 ] - 1.5f, rect[ 3 ] - 1.5f, paint2 );
            }
        }
    }
    private void onDrawLine( Canvas canvas )
    {
        if( m_status == STA_LINE && m_rects != null )
        {
            int len = m_rects.length;
            int cur;
            Paint paint1 = new Paint( );
            paint1.setStyle( Style.STROKE );
            paint1.setStrokeWidth( 3 );
            paint1.setARGB( 0x80, 0xFF, 0, 0 );
            for( cur = 0; cur < len; cur += 4 )
            {
                canvas.drawLine( m_rects[ cur ], m_rects[ cur + 1 ], m_rects[ cur + 2 ], m_rects[ cur + 3 ], paint1 );
            }
        }
    }
    private void onDrawEllipse( Canvas canvas )
    {
        if( m_status == STA_ELLIPSE && m_rects != null )
        {
            int len = m_rects.length;
            int cur;
            Paint paint1 = new Paint( );
            Paint paint2 = new Paint( );
            paint1.setStyle( Style.STROKE );
            paint1.setStrokeWidth( 3 );
            paint1.setARGB( 0x80, 0xFF, 0, 0 );
            paint2.setStyle( Style.FILL );
            paint2.setARGB( 0x80, 0, 0, 0xFF );
            for( cur = 0; cur < len; cur += 4 )
            {
                float rect[] = new float[4];
                if( m_rects[ cur ] > m_rects[ cur + 2 ] )
                {
                    rect[ 0 ] = m_rects[ cur + 2 ];
                    rect[ 2 ] = m_rects[ cur ];
                }
                else
                {
                    rect[ 0 ] = m_rects[ cur ];
                    rect[ 2 ] = m_rects[ cur + 2 ];
                }
                if( m_rects[ cur + 1 ] > m_rects[ cur + 3 ] )
                {
                    rect[ 1 ] = m_rects[ cur + 3 ];
                    rect[ 3 ] = m_rects[ cur + 1 ];
                }
                else
                {
                    rect[ 1 ] = m_rects[ cur + 1 ];
                    rect[ 3 ] = m_rects[ cur + 3 ];
                }
                RectF rc = new RectF( );
                rc.left = rect[ 0 ];
                rc.top = rect[ 1 ];
                rc.right = rect[ 2 ];
                rc.bottom = rect[ 3 ];
                canvas.drawOval( rc, paint1 );
                rc.left += 1.5f;
                rc.top += 1.5f;
                rc.right -= 1.5f;
                rc.bottom -= 1.5f;
                canvas.drawOval( rc, paint2 );
            }
        }
    }
    @Override
    public void computeScroll( )
    {
        if( m_view == null )
            return;
        m_view.vComputeScroll( );
    }
    @Override
    protected void onDraw( Canvas canvas )
    {
        if( m_view == null )
            return;
        m_view.vDraw( canvas );
        onDrawAnnot( canvas );
        onDrawRect( canvas );
        onDrawEllipse( canvas );
        onDrawAnnot( canvas );
        onDrawLine( canvas );
        if( m_status == STA_INK )
        {
            m_ink.OnDraw( canvas, 0, 0 );
        }
        ActivityManager mgr = ( ActivityManager )getContext( ).getSystemService( Context.ACTIVITY_SERVICE );
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo( );
        mgr.getMemoryInfo( info );
        Paint paint = new Paint( );
        paint.setARGB( 255, 255, 0, 0 );
        paint.setTextSize( 30 );
        canvas.drawText( "AvialMem:" + info.availMem / ( 1024 * 1024 ) + " M", 20, 150, paint );
    }
    @Override
    protected void onSizeChanged( int w, int h, int oldw, int oldh )
    {
        super.onSizeChanged( w, h, oldw, oldh );
        if( m_view == null )
            return;
        m_view.vResize( w, h );
    }

    private void capturarDatos( MotionEvent event )
    {
        String captura = "";
        captura = display.velocityTrack( event );
        float valorx = 0;
        float valory = 0;
        if( captura != "" && captura != null )
        {
            String partexy[] = captura.split( ":" );
            String valores[] = partexy[ 1 ].split( ";" );
            valorx = Float.parseFloat( valores[ 0 ] );
            valory = Float.parseFloat( valores[ 1 ] );
        }
        int a = ( int )valorx;
        int b = ( int )valory;

        String rtaX = display.analizarVelocidad( a, "X" );
        String rtaY = display.analizarVelocidad( b, "Y" );

        if( captura != "" )
        {
            datos = DatosDisplay.darInstacia( );
            datos.setAnalisiX( rtaX );
            datos.setAnalisiY( rtaY );
            String rta = captura + ";" + rtaX + ";" + rtaY + ";";
            if( rta.contains( "LECTURA" ) )
            {
                datos.setLecturas( rta );
                RelativeLayout layout = (RelativeLayout)this.getParent();
                TextView txtMove = (TextView)layout.findViewById(R.id.txtMove);
                txtMove.clearComposingText( );
                txtMove.setText(rta);
            }
        }
    }

    @Override
    public boolean onTouchEvent( MotionEvent event )
    {
        capturarDatos( event );
        if( m_view == null )
            return false;
        if( m_view.vGetLock( ) == 3 )
        {
            if( m_status == STA_INK )
            {
                switch( event.getActionMasked( ) )
                {
                    case MotionEvent.ACTION_DOWN:
                        if( m_ink_page == null )
                        {
                            PDFPos pos = m_view.vGetPos( ( int )event.getX( ), ( int )event.getY( ) );
                            m_ink_page = m_view.vGetPage( pos.pageno );
                        }
                        m_ink.OnDown( event.getX( ), event.getY( ) );
                        break;
                    case MotionEvent.ACTION_MOVE:
                        m_ink.OnMove( event.getX( ), event.getY( ) );
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        m_ink.OnUp( event.getX( ), event.getY( ) );
                        break;
                }
                invalidate( );
                return true;
            }
            if( m_status == STA_RECT )
            {
                int len = 0;
                if( m_rects != null )
                    len = m_rects.length;
                int cur = 0;
                switch( event.getActionMasked( ) )
                {
                    case MotionEvent.ACTION_DOWN:
                        float rects[] = new float[len + 4];
                        for( cur = 0; cur < len; cur++ )
                            rects[ cur ] = m_rects[ cur ];
                        len += 4;
                        rects[ cur + 0 ] = event.getX( );
                        rects[ cur + 1 ] = event.getY( );
                        rects[ cur + 2 ] = event.getX( );
                        rects[ cur + 3 ] = event.getY( );
                        m_rects = rects;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        m_rects[ len - 2 ] = event.getX( );
                        m_rects[ len - 1 ] = event.getY( );
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        m_rects[ len - 2 ] = event.getX( );
                        m_rects[ len - 1 ] = event.getY( );
                        break;
                }
                invalidate( );
                return true;
            }
            if( m_status == STA_ELLIPSE )
            {
                int len = 0;
                if( m_rects != null )
                    len = m_rects.length;
                int cur = 0;
                switch( event.getActionMasked( ) )
                {
                    case MotionEvent.ACTION_DOWN:
                        float rects[] = new float[len + 4];
                        for( cur = 0; cur < len; cur++ )
                            rects[ cur ] = m_rects[ cur ];
                        len += 4;
                        rects[ cur + 0 ] = event.getX( );
                        rects[ cur + 1 ] = event.getY( );
                        rects[ cur + 2 ] = event.getX( );
                        rects[ cur + 3 ] = event.getY( );
                        m_rects = rects;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        m_rects[ len - 2 ] = event.getX( );
                        m_rects[ len - 1 ] = event.getY( );
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        m_rects[ len - 2 ] = event.getX( );
                        m_rects[ len - 1 ] = event.getY( );
                        break;
                }
                invalidate( );
                return true;
            }
            if( m_status == STA_NOTE )
            {
                switch( event.getActionMasked( ) )
                {
                    case MotionEvent.ACTION_UP:
                        PDFPos pos = m_view.vGetPos( ( int )event.getX( ), ( int )event.getY( ) );
                        PDFVPage vpage = m_view.vGetPage( pos.pageno );
                        Page page = vpage.GetPage( );
                        if( page != null )
                        {
                            float pt[] = new float[2];
                            pt[ 0 ] = pos.x;
                            pt[ 1 ] = pos.y;
                            page.AddAnnotText( pt );
                            m_view.vRender( vpage );
                        }
                        break;
                }
                return true;
            }
            if( m_status == STA_LINE )
            {
                int len = 0;
                if( m_rects != null )
                    len = m_rects.length;
                int cur = 0;
                switch( event.getActionMasked( ) )
                {
                    case MotionEvent.ACTION_DOWN:
                        float rects[] = new float[len + 4];
                        for( cur = 0; cur < len; cur++ )
                            rects[ cur ] = m_rects[ cur ];
                        len += 4;
                        rects[ cur + 0 ] = event.getX( );
                        rects[ cur + 1 ] = event.getY( );
                        rects[ cur + 2 ] = event.getX( );
                        rects[ cur + 3 ] = event.getY( );
                        m_rects = rects;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        m_rects[ len - 2 ] = event.getX( );
                        m_rects[ len - 1 ] = event.getY( );
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        m_rects[ len - 2 ] = event.getX( );
                        m_rects[ len - 1 ] = event.getY( );
                        break;
                }
                invalidate( );
                return true;
            }
        }
        return m_view.vTouchEvent( event );
    }
    public void PDFGotoPage( int pageno )
    {
        m_view.vGotoPage( pageno );
    }
    public Document PDFGetDoc( )
    {
        return m_doc;
    }
}
