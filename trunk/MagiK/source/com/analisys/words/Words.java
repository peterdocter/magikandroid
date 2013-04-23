package com.analisys.words;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

import android.util.Log;

import com.aliasi.lm.TokenizedLM;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.Files;
import com.aliasi.util.ScoredObject;

/**
 * Creates queries to the SQLite helper and returns its results.
 * @author Kelvin Guerrero
 * 
 */
public class Words
{

    private static int NGRAM = 3;
    private static int MIN_COUNT = 5;
    private static int MAX_NGRAM_REPORTING_LENGTH = 2;
    private static int NGRAM_REPORTING_LENGTH = 2;
    private static int MAX_COUNT = 100;
    static File root = android.os.Environment.getExternalStorageDirectory( );
    private static File BACKGROUND_DIR = new File( root.getAbsolutePath( ) + "/Magik/train" );
    private static File FOREGROUND_DIR = new File( root.getAbsolutePath( ) + "/Magik/test" );

    public void getHtml( ) throws ClientProtocolException, IOException
    {
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters,3000); // 3s max for connection
        HttpConnectionParams.setSoTimeout(httpParameters, 4000); // 4s max to get data
        HttpClient httpclient = new DefaultHttpClient(httpParameters);
        HttpGet httpget = new HttpGet( "http://www.eltiempo.com/gente/reese-witherspoon-legalmente-rubia-detenida-en-est_12754682-4" ); // Set the action you want to do
        HttpResponse response = httpclient.execute( httpget ); // Executeit
        HttpEntity entity = response.getEntity( );
        InputStream is = entity.getContent( ); // Create an InputStream with the response
        BufferedReader reader = new BufferedReader( new InputStreamReader( is, "iso-8859-1" ), 8 );
        StringBuilder sb = new StringBuilder( );
        String line = null;
        
        while( ( line = reader.readLine( ) ) != null )
        {
            String txt = Jsoup.parse(line).text();
            sb.append( txt + "\n" );
        }
        String resString = sb.toString( ); // Result is here

        is.close( ); // Close the stream
        
        File root = android.os.Environment.getExternalStorageDirectory( );
        Log.i( "STORAGE", "External file system root: " + root );
        File dir = new File( root.getAbsolutePath( ) + "/Magik/" + "train" );
        dir.mkdirs( );
        File monitorFile = new File( dir, "datos3" );
        try
        {

            FileOutputStream f = new FileOutputStream( monitorFile, true );
            PrintWriter pw = new PrintWriter( f );
            pw.append( resString );
            pw.append( "\n" );
            pw.flush( );
            pw.close( );
            f.close( );
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
    }
    public Words( )
    {
        try
        {
            getHtml( );
        }
        catch( ClientProtocolException e1 )
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        catch( IOException e1 )
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try
        {
            TokenizerFactory tokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE;

            System.out.println( "Training background model" );
            TokenizedLM backgroundModel;

            backgroundModel = buildModel( tokenizerFactory, NGRAM, BACKGROUND_DIR );

            backgroundModel.sequenceCounter( ).prune( 3 );

            System.out.println( "\nAssembling collocations in Training" );
            SortedSet<ScoredObject<String[]>> coll = backgroundModel.collocationSet( NGRAM_REPORTING_LENGTH, MIN_COUNT, MAX_COUNT );

            System.out.println( "\nCollocations in Order of Significance:" );
            report( coll );

            System.out.println( "Training foreground model" );
            TokenizedLM foregroundModel = buildModel( tokenizerFactory, NGRAM, FOREGROUND_DIR );
            foregroundModel.sequenceCounter( ).prune( 3 );

            System.out.println( "\nAssembling New Terms in Test vs. Training" );
            SortedSet<ScoredObject<String[]>> newTerms = foregroundModel.newTermSet( NGRAM_REPORTING_LENGTH, MIN_COUNT, MAX_COUNT, backgroundModel );

            System.out.println( "\nNew Terms in Order of Signficance:" );
            report( newTerms );

            System.out.println( "\nDone." );
        }
        catch( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace( );
        }
    }
    private static TokenizedLM buildModel( TokenizerFactory tokenizerFactory, int ngram, File directory ) throws IOException
    {

        String[] trainingFiles = directory.list( );
        TokenizedLM model = new TokenizedLM( tokenizerFactory, ngram );
        System.out.println( "Training on " + directory );

        for( int j = 0; j < trainingFiles.length; ++j )
        {
            String text = Files.readFromFile( new File( directory, trainingFiles[ j ] ), "ISO-8859-1" );
            model.handle( text );
        }
        return model;
    }

    private static void report( SortedSet<ScoredObject<String[]>> nGrams )
    {
        for( ScoredObject<String[]> nGram : nGrams )
        {
            double score = nGram.score( );
            String[] toks = nGram.getObject( );
            report_filter( score, toks );
        }
    }

    private static void report_filter( double score, String[] toks )
    {
        String accum = "";
        for( int j = 0; j < toks.length; ++j )
        {
            if( nonCapWord( toks[ j ] ) )
                return;
            accum += " " + toks[ j ];
        }
        System.out.println( "Score: " + score + " with :" + accum );
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