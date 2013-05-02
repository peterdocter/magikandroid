package com.radaee.reader;

import com.radaee.pdf.*;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

/**
 * test activity for PDFInk<br/>
 * PDFInk test HWriting class.
 * @author Radaee
 */
public class PDFViewerAct extends Activity implements OnChildClickListener
{
	private Document m_doc = null;
	private PDFViewer m_vPDF = null;
	private SnatchView m_vFiles = null;
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Global.Init( this );
		m_vFiles = new SnatchView(this);
		m_vFiles.setOnChildClickListener(this);
		m_vFiles.start();
		m_vPDF = new PDFViewer(this);
		setContentView(m_vFiles);
    }
    protected void onDestroy()
    {
    	if( m_vPDF != null )
    		m_vPDF.Close();
    	Global.RemoveTmp();
    	super.onDestroy();
    }
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id)
	{
		String val = m_vFiles.getChildPath(groupPosition, childPosition);
		if( val != null )
		{
			if( m_doc != null )
			{
				m_vPDF.Close();
				m_doc.Close();
				m_doc = null;
			}
			m_doc = new Document();
			int ret = m_doc.Open(val, null);
			switch( ret )
			{
			case -1://need input password
				finish();
				break;
			case -2://unknown encryption
				finish();
				break;
			case -3://damaged or invalid format
				finish();
				break;
			case -10://access denied or invalid file path
				finish();
				break;
			case 0://succeeded, and continue
				break;
			default://unknown error
				finish();
				break;
			}
			m_vPDF.Open( 0, m_doc );
            setContentView(m_vPDF);
		}
		return false;
	}
}