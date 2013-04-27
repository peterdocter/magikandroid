package com.analisys.words;

import java.util.ArrayList;

public class PalabrasClave
{
    private ArrayList<String> palabras;
    
    private static PalabrasClave instancia;
    
    public PalabrasClave( )
    {
        palabras = new ArrayList<String>( );
    }
    
    public static PalabrasClave darInstacia()
    {
        if( instancia == null )
        {
            instancia =  new PalabrasClave( );
        }
        return instancia;
    }

    public ArrayList<String> getPalabras( )
    {
        return palabras;
    }

    public void setPalabras( String palabras )
    {
        this.palabras.add( palabras );
    }
}
