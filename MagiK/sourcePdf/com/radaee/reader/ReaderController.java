package com.radaee.reader;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import com.radaee.pdf.Document;
import com.radaee.pdfex.*;

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
    public ReaderController(Context context)
    {
        super(context);
    }

    public ReaderController(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
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

    @Override
	protected void onSizeChanged (int w, int h, int oldw, int oldh)
    {
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

    @Override
	public void onSingleTap(float x, float y) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
	public void onInvalidate()
    {
        if( m_pdv != null )
        {
            invalidate();
        }
    }

    @Override
	public void onFound(boolean found) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
	public void onSelectStart() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
	public void onSelectEnd(String text) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
	public void onSelDisplayed(PDFView.PDFSelDispPara para) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
	public void onSubmit(String target, String para) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
	public void onOpenURL(String url) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
	public void onOpen3D(String file_name) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
	public void onOpenMovie(String file_name) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
	public void onOpenSound(int[] paras, String file_name) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
	public void onOpenAttachment(String file_name) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
	public void onPageChanged(int pageno) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
	public void onPageDisplayed(PDFView.PDFPageDispPara para) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
