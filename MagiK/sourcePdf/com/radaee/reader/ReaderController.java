package com.radaee.reader;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.radaee.pdf.Document;
import com.radaee.pdf.Global;
import com.radaee.pdf.Matrix;
import com.radaee.pdf.Page;
import com.radaee.pdfex.*;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Erick
 * Date: 1/21/13
 * Time: 11:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReaderController extends View implements PDFView.PDFViewListener {

    PDFView m_pdv = new PDFViewSingle();
    public boolean m_lock_resize = false;
    private int m_save_w = 0;
    private int m_save_h = 0;
    private int m_cur_page = 0;

    public ReaderController(Context context)
    {
        super(context);
    }

    public ReaderController(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    protected void onDraw(Canvas canvas)
    {
        m_pdv.viewDraw(canvas);

        // Paint paint = new Paint();
        // paint.setARGB(255, 255, 0, 0);
        // canvas.drawText("myText", 20, 20, paint);
    }

    public void open(Document doc)
    {
        m_pdv = new PDFViewVert();
        m_pdv.viewOpen(this.getContext(), doc, 0xFFCCCCCC, 4);
        m_pdv.viewSetViewListener(this);
        invalidate();
    }

    protected void onSizeChanged (int w, int h, int oldw, int oldh)
    {
        m_save_w = w;
        m_save_h = h;
        if( m_pdv != null && !m_lock_resize )
            m_pdv.viewResize(w, h);
    }

    public void close()
    {
        if (m_pdv != null)
        {
            m_pdv.viewClose();
        }
        m_pdv = null;
    }

    public void onSingleTap(float x, float y) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onInvalidate()
    {
        if( m_pdv != null )
        {
            invalidate();
        }
    }

    public void onFound(boolean found) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onSelectStart() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onSelectEnd(String text) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onSelDisplayed(PDFView.PDFSelDispPara para) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onSubmit(String target, String para) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onOpenURL(String url) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onOpen3D(String file_name) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onOpenMovie(String file_name) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onOpenSound(int[] paras, String file_name) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onOpenAttachment(String file_name) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onPageChanged(int pageno) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onPageDisplayed(PDFView.PDFPageDispPara para) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
