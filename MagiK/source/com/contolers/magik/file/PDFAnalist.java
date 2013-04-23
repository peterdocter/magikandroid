/**
 * 
 */
package com.contolers.magik.file;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;


/**
 * @author User
 *
 */
public class PDFAnalist {
	
	public PDFAnalist()
	{
		
	}
	
	public String getPDFText(String path) {
		String text = null;
	    try {
	    	PdfReader reader = new PdfReader(path);
	    	int pages = reader.getNumberOfPages();
	    	text = "";	    	
	    	for(int i = 0; i < pages; i ++)
	    	{
	    		text+= PdfTextExtractor.getTextFromPage(reader, i);
	    	}			
		} catch (Exception e) {
			// TODO: handle exception
		}
	    return text;
	}

}
