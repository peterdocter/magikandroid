package net.sf.andpdf.pdfviewer;

import java.util.ArrayList;

public class DatosDisplay 
{
        private ArrayList<String> analisiX;
        private ArrayList<String> analisiY;
        private static DatosDisplay instancia;

        public DatosDisplay( )
        {
            analisiX = new ArrayList<String>( );
            analisiY = new ArrayList<String>( );
        }
        
        public static DatosDisplay darInstacia()
        {
            if( instancia == null )
            {
                instancia =  new DatosDisplay( );
            }
            return instancia;
        }

        public ArrayList<String> getAnalisiX( )
        {
            return analisiX;
        }

        public void setAnalisiX( String analisiX )
        {
            this.analisiX.add( analisiX );
        }

        public ArrayList<String> getAnalisiY( )
        {
            return analisiY;
        }

        public void setAnalisiY( String analisiY )
        {
            this.analisiY.add( analisiY );
        }
}
