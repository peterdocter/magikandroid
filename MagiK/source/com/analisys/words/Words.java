package com.analisys.words;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.SortedSet;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.jsoup.Jsoup;

import android.R.bool;
import android.util.Log;

import com.aliasi.lm.TokenizedLM;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.Files;
import com.aliasi.util.ScoredObject;
import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.Language;
import com.example.magik.monitoring.DatosDisplay;

/**
 * Creates queries to the SQLite helper and returns its results.
 * @author Kelvin Guerrero
 * 
 */
public class Words
{

    private ArrayList<String> tokens;
    private ArrayList<String> leguaje;
    private ArrayList<Language> leguajeL;
    private static int NGRAM = 3;
    private static int MIN_COUNT = 5;
    private static int MAX_NGRAM_REPORTING_LENGTH = 2;
    private static int NGRAM_REPORTING_LENGTH = 2;
    private static int MAX_COUNT = 100;
    static File root = android.os.Environment.getExternalStorageDirectory( );
    private static File BACKGROUND_DIR = new File( root.getAbsolutePath( ) + "/Magik/train" );
    private static File FOREGROUND_DIR = new File( root.getAbsolutePath( ) + "/Magik/test" );

    private boolean error = false;
    public ArrayList<String> getLenguaje( )
    {
        return leguaje;
    }

    private void cargarlenguajes( )
    {
        try
        {
            File root = android.os.Environment.getExternalStorageDirectory( );
            DetectorFactory.clear( );
            DetectorFactory.loadProfile( root.getAbsolutePath( ) + "/Magik/profiles" );
        }
        catch( LangDetectException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace( );
            error = true;
        }
    }

    private void lenguaje( String texto )
    {
        try
        {
            cargarlenguajes( );
            Detector detector = DetectorFactory.create( );
            detector.append( texto );
            String temp;
            ArrayList<Language> langlist = detector.getProbabilities( );
            leguajeL = langlist;
            for( int i = 0; i < langlist.size( ); i++ )
            {
                temp = langlist.get( i ).lang + ";" + langlist.get( i ).prob;
                leguaje = new ArrayList<String>( );
                leguaje.add( temp );
            }

            langlist = null;
            temp = null;
            detector = null;
            // root = null;
            DetectorFactory.clear( );
            System.gc( );
        }
        catch( LangDetectException e )
        {
            System.out.println( e.getMessage( ) );
            e.printStackTrace( );
        }

    }

    public void getHtml( String url ) throws ClientProtocolException, IOException
    {
        HttpParams httpParameters = new BasicHttpParams( );
        HttpConnectionParams.setConnectionTimeout( httpParameters, 3000 ); // 3s max for connection
        HttpConnectionParams.setSoTimeout( httpParameters, 4000 ); // 4s max to get data
        HttpClient httpclient = new DefaultHttpClient( httpParameters );
        HttpGet httpget = new HttpGet( url ); // Set the action you want to do
        HttpResponse response = httpclient.execute( httpget ); // Executeit
        HttpEntity entity = response.getEntity( );
        InputStream is = entity.getContent( ); // Create an InputStream with the response
        BufferedReader reader = new BufferedReader( new InputStreamReader( is, "iso-8859-1" ), 8 );
        StringBuilder sb = new StringBuilder( );
        String line = null;
        String txt;
        while( ( line = reader.readLine( ) ) != null )
        {
            txt = Jsoup.parse( line ).text( );
            if( txt != "" || txt != null )
            {
                sb.append( txt + "\n" );
            }
        }
        String resString = sb.toString( ); // Result is here
        sb = null;
        txt = null;
        reader = null;
        entity = null;
        httpget = null;
        httpclient = null;
        httpParameters = null;
        System.gc( );

        lenguaje( resString );

        if( !error )
        {

            is.close( ); // Close the stream

            File root = android.os.Environment.getExternalStorageDirectory( );
            Log.i( "STORAGE", "External file system root: " + root );
            String lng = leguajeL.get( 0 ).lang;
            File dir = new File( root.getAbsolutePath( ) + "/Magik/" + "train" + lng );
            BACKGROUND_DIR = dir;
            File dir2 = new File( root.getAbsolutePath( ) + "/Magik/" + "test" + lng );
            FOREGROUND_DIR = dir2;

            String[] children = dir2.list( );
            if( children != null && children.length > 0 )
            {
                for( int i = 0; i < children.length; i++ )
                {
                    new File( dir2, children[ i ] ).delete( );
                }
            }

            dir.mkdirs( );
            dir2.mkdirs( );

            File monitorFile = new File( dir, "datos" + ( dir.listFiles( ).length + 1 ) );
            File monitorFile2 = new File( dir2, "datos" + ( dir2.listFiles( ).length + 1 ) );
            try
            {

                FileOutputStream f = new FileOutputStream( monitorFile, true );
                PrintWriter pw = new PrintWriter( f );
                pw.append( resString );
                pw.flush( );
                pw.close( );
                f.close( );

                FileOutputStream f2 = new FileOutputStream( monitorFile2, true );
                PrintWriter pw2 = new PrintWriter( f2 );
                pw2.append( resString );
                pw2.flush( );
                pw2.close( );
                f2.close( );

            }
            catch( FileNotFoundException e )
            {
                e.printStackTrace( );
                Log.i( "PROBLEMA", "******* File not found. Did you" + " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?" );
            }
            catch( IOException e )
            {
                e.printStackTrace( );
            }
            resString = null;
            System.gc( );
        }
    }

    public Words(  )
    {
        tokens = new ArrayList<String>( );
    }

    public boolean startWords( String url )
    {
        try
        {
            getHtml( url );
        }
        catch( ClientProtocolException e1 )
        {
            // TODO Auto-generated catch block
            e1.printStackTrace( );
        }
        catch( IOException e1 )
        {
            // TODO Auto-generated catch block
            e1.printStackTrace( );
        }
        try
        {
            if( !error )
            {
                TokenizerFactory tokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE;

                System.out.println( "Training background model" );
                TokenizedLM backgroundModel;

                backgroundModel = buildModel( tokenizerFactory, NGRAM, BACKGROUND_DIR );

                backgroundModel.sequenceCounter( ).prune( 3 );

                System.out.println( "\nAssembling collocations in Training" );
                SortedSet<ScoredObject<String[]>> coll = backgroundModel.collocationSet( NGRAM_REPORTING_LENGTH, MIN_COUNT, MAX_COUNT );

                System.out.println( "\nCollocations in Order of Significance:" );
                // report( coll );

                System.out.println( "Training foreground model" );
                TokenizedLM foregroundModel = buildModel( tokenizerFactory, NGRAM, FOREGROUND_DIR );
                foregroundModel.sequenceCounter( ).prune( 3 );

                System.out.println( "\nAssembling New Terms in Test vs. Training" );
                SortedSet<ScoredObject<String[]>> newTerms = foregroundModel.newTermSet( NGRAM_REPORTING_LENGTH, MIN_COUNT, MAX_COUNT, backgroundModel );

                System.out.println( "\nNew Terms in Order of Signficance:" );
                report( newTerms );

                foregroundModel = null;
                backgroundModel = null;
                coll = null;
                tokenizerFactory = null;
                newTerms = null;
                System.gc( );
                System.out.println( "\nDone." );
                return true;
            }
            return false;
        }
        catch( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace( );
        }
        return false;

    }

    private static TokenizedLM buildModel( TokenizerFactory tokenizerFactory, int ngram, File directory ) throws IOException
    {

        String[] trainingFiles = directory.list( );
        TokenizedLM model = new TokenizedLM( tokenizerFactory, ngram );
        System.out.println( "Training on " + directory );
        String text;
        for( int j = 0; j < trainingFiles.length; ++j )
        {
            text = Files.readFromFile( new File( directory, trainingFiles[ j ] ), "ISO-8859-1" );
            model.handle( text );
        }
        text = null;
        trainingFiles = null;
        System.gc( );
        return model;
    }

    private static void report( SortedSet<ScoredObject<String[]>> nGrams )
    {
        for( ScoredObject<String[]> nGram : nGrams )
        {
            report_filter( nGram.score( ), nGram.getObject( ) );
        }
        System.gc( );
    }

    private static void report_filter( double score, String[] toks )
    {
        PalabrasClave palabrasClave = PalabrasClave.darInstacia( );
        String accum = "";
        for( int j = 0; j < toks.length; ++j )
        {
            if( nonCapWord( toks[ j ] ) )
                return;
            accum += " " + toks[ j ];
        }
        if( accum != null || accum != "" )
        {
            palabrasClave.setPalabras( accum );
            System.out.println( "Score: " + score + " with :" + accum );
        }
    }

    private static boolean nonCapWord( String tok )
    {
        if( !Character.isUpperCase( tok.charAt( 0 ) ) )
            return true;
        for( int i = 1; i < tok.length( ); ++i )
            if( !Character.isLowerCase( tok.charAt( i ) ) )
                return true;
        return false;
    }

}