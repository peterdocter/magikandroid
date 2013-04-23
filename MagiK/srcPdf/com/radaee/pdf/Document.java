package com.radaee.pdf;
/**
class for PDF Document.
@author Radaee
@version 1.1
*/
public class Document
{
	public interface PDFStream
	{
		/**
		 * check whether the stream is writable 
		 * @return true or false
		 */
		public boolean writeable();
		/**
		 * get stream length.
		 * @return
		 */
		public int get_size();
		/**
		 * read data from stream
		 * @param data output values.
		 * @return bytes read
		 */
		public int read( byte[] data );
		/**
		 * write data to stream
		 * @param data data to write
		 * @return bytes written
		 */
		public int write( byte[] data );
		/**
		 * seek to position
		 * @param pos position from begin of the stream
		 */
		public void seek( int pos );
		/**
		 * tell current position
		 * @return position from begin of the stream
		 */
		public int tell();
	}
	protected int hand_val = 0;
	private int page_count = 0;
	private static native int open( String path, String password );
	private static native int openMem( byte[] data, String password );
	private static native int openStream( PDFStream stream, String password );
	private static native int getPermission( int hand );
	private static native int getPerm( int hand );
	private static native void close( int hand );
	private static native int getPage( int hand, int pageno );
	private static native int getPageCount( int hand );
	private static native float getPageWidth( int hand, int pageno );
	private static native float getPageHeight( int hand, int pageno );
	private static native String getOutlineTitle( int hand, int outline );
	private static native int getOutlineDest( int hand, int outline );
	private static native int getOutlineNext( int hand, int outline );
	private static native int getOutlineChild( int hand, int outline );
	private static native String getMeta( int hand, String tag );
	private static native boolean setMeta( int hand, String tag, String value );
	private static native boolean canSave( int hand );
	private static native boolean save( int hand );
	private static native boolean saveAs( int hand, String dst );//remove security info and save to another file.
	private static native boolean isEncrypted( int hand );
	public Document()
	{
	}
	/**
	 * inner function.
	 * @param vals
	 */
	public Document(int vals[] )
	{
		if( vals != null )
		{
			hand_val = vals[0];
			page_count = vals[1];
		}
	}
	/**
	 * inner function.
	 * @return inner value
	 */
	public int[] getVals()
	{
		int vals[] = new int[2];
		vals[0] = hand_val;
		vals[1] = vals[1];
		return vals;
	}
	private int getOutlineRoot( int hand )
	{
		return getOutlineNext( hand_val, 0 );
	}
	/**
	 * check if opened.
	 * @return true or false.
	 */
	public boolean is_opened()
	{
		return (hand_val != 0);
	}
	/**
	 * open document.<br/>
	 * first time, SDK try password as user password, and then try password as owner password.
	 * @param path PDF file to be open.
	 * @param password password or null.
	 * @return error code:<br/>
	 * 0:succeeded, and continue<br/>
	 * -1:need input password<br/>
	 * -2:unknown encryption<br/>
	 * -3:damaged or invalid format<br/>
	 * -10:access denied or invalid file path<br/>
	 * others:unknown error
	 */
	public int Open( String path, String password )
	{
		if( hand_val == 0 )
		{
			int ret = 0;
			hand_val = open( path, password );
			if( hand_val <= 0 )//error
			{
				ret = hand_val;
				hand_val = 0;
				page_count = 0;
			}
			else
				page_count = getPageCount(hand_val);
			return ret;
		}
		return 0;
	}
	/**
	 * open document in memory.
	 * first time, SDK try password as user password, and then try password as owner password.
	 * @param data data for whole PDF file in byte array. developers should retain array data, till document closed.
	 * @param password password or null.
	 * @return error code:<br/>
	 * 0:succeeded, and continue<br/>
	 * -1:need input password<br/>
	 * -2:unknown encryption<br/>
	 * -3:damaged or invalid format<br/>
	 * -10:access denied or invalid file path<br/>
	 * others:unknown error
	 */
	public int OpenMem( byte[] data, String password )
	{
		if( hand_val == 0 )
		{
			int ret = 0;
			hand_val = openMem( data, password );
			if( hand_val <= 0 )//error
			{
				ret = hand_val;
				hand_val = 0;
				page_count = 0;
			}
			else
				page_count = getPageCount(hand_val);
			return ret;
		}
		return 0;
	}
	/**
	 * open document from stream.
	 * first time, SDK try password as user password, and then try password as owner password.
	 * @param stream PDFStream object.
	 * @param password password or null.
	 * @return error code:<br/>
	 * 0:succeeded, and continue<br/>
	 * -1:need input password<br/>
	 * -2:unknown encryption<br/>
	 * -3:damaged or invalid format<br/>
	 * -10:access denied or invalid file path<br/>
	 * others:unknown error
	 */
	public int OpenStream( PDFStream stream, String password )
	{
		if( hand_val == 0 )
		{
			int ret = 0;
			hand_val = openStream( stream, password );
			if( hand_val <= 0 )//error
			{
				ret = hand_val;
				hand_val = 0;
				page_count = 0;
			}
			else
				page_count = getPageCount(hand_val);
			return ret;
		}
		return 0;
	}
	/**
	 * get permission of PDF, this value defined in PDF reference 1.7<br/>
	 * bit 1-2 reserved<br/>
	 * bit 3(0x4) print<br/>
	 * bit 4(0x8) modify<br/>
	 * bit 5(0x10) extract text or image<br/>
	 * others: see PDF reference
	 * @return permission flags
	 */
	public int GetPermission()
	{
		return getPermission( hand_val );
	}
	/**
	 * get permission of PDF, this value defined in "Perm" entry in Catalog object
	 * @return 0 means not defined<br/>
	 * 1 means can't modify<br/>
	 * 2 means can modify some form fields<br/>
	 * 3 means can do any modify<br/>
	 */
	public int GetPerm()
	{
		return getPerm( hand_val );
	}
	/**
	 * close the document.
	 */
	public void Close()
	{
		if( hand_val != 0 )
			close( hand_val );
		hand_val = 0;
		page_count = 0;
	}
	/**
	 * get a Page object for page NO.
	 * @param pageno 0 based page NO. range:[0, GetPageCount()-1]
	 * @return Page object
	 */
	public Page GetPage( int pageno )
	{
		if( hand_val == 0 ) return null;
		int hand = getPage( hand_val, pageno );
		if( hand == 0 ) return null;
		Page page = new Page();
		if( page != null ) page.hand = hand;
		return page;
	}
	/**
	 * get pages count.
	 * @return pages count.
	 */
	public int GetPageCount()
	{
		//it loads all pages. sometimes the function is very slow.
		return page_count;
	}
	/**
	 * get page width by page NO.
	 * @param pageno 0 based page NO. range:[0, GetPageCount()-1]
	 * @return width value.
	 */
	public float GetPageWidth( int pageno )
	{
		float w = getPageWidth( hand_val, pageno );
		if( w <= 0 ) return 1;
		else return w;
	}
	/**
	 * get page height by page NO.
	 * @param pageno 0 based page NO. range:[0, GetPageCount()-1]
	 * @return height value.
	 */
	public float GetPageHeight( int pageno )
	{
		float h = getPageHeight( hand_val, pageno );
		if( h <= 0 ) return 1;
		else return h;
	}
	/**
	 * get meta data for document.
	 * @param tag Predefined values:"Title", "Author", "Subject", "Keywords", "Creator", "Producer", "CreationDate", "ModDate".<br/>or you can pass any key that self-defined.
	 * @return Meta string value, or null.
	 */
	public String GetMeta( String tag )
	{
		return getMeta( hand_val, tag );
	}
	/**
	 * set meta data for document.<br/>
	 * this method valid only in premium version.
	 * @param tag Predefined values:"Title", "Author", "Subject", "Keywords", "Creator", "Producer", "CreationDate", "ModDate".<br/>or you can pass any key that self-defined.
	 * @param val string value.
	 * @return true or false.
	 */
	public boolean SetMeta( String tag, String val )
	{
		return setMeta( hand_val, tag, val );
	}
	/**
	 * get root outline item.
	 * @return handle value of first root outline item. or 0 if no outlines.<br/>
	 */
	public int GetOutlines()
	{
		return getOutlineRoot(hand_val);
	}
	/**
	 * get goto page NO.
	 * @param outline handle value of outline item.
	 * @return 0 based page NO. or -1 if failed.
	 */
	public int GetOutlineDest( int outline )
	{
		//return the page NO.
		//from 0 to page_count - 1 
		return getOutlineDest( hand_val, outline );
	}
	/**
	 * get next outline item
	 * @param outline handle value of current outline item.
	 * @return handle value of next outline item. or 0 if no next item.
	 */
	public int GetOutlineNext( int outline )
	{
		return getOutlineNext( hand_val, outline );
	}
	/**
	 * get first child outline item.
	 * @param outline handle value of parent outline item.
	 * @return handle value of child outline item. or 0 if no child.
	 */
	public int GetOutlineChild( int outline )
	{
		return getOutlineChild( hand_val, outline );
	}
	/**
	 * get label of the outline item
	 * @param outline handle value of outline item.
	 * @return label of outline item or null.
	 */
	public String GetOutlineTitle(int outline)
	{
		return getOutlineTitle( hand_val, outline );
	}
	/**
	 * check if document can be modified or saved.<br/>
	 * this always return false, if no license actived.
	 * @return true or false.
	 */
	public boolean CanSave()
	{
		return canSave( hand_val );
	}
	/**
	 * save the document.<br/>
	 * this always return false, if no license actived.
	 * @return true or false
	 */
	public boolean Save()
	{
		return save( hand_val );
	}
	/**
	 * save as the document to another file. it remove any security information.<br/>
	 * this always return false, if no license actived.
	 * @param path path to save.
	 * @return true or false.
	 */
	public boolean SaveAs( String path )
	{
		return saveAs( hand_val, path );
	}
	/**
	 * check if document is encrypted.
	 * @return true or false.
	 */
	public boolean IsEncrypted()
	{
		return isEncrypted( hand_val );
	}
}
