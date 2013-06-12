package com.magik.db;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Creates queries to the SQLite helper and returns its results.
 * @author Kelvin Guerrero
 * 
 */
public class PersistenceManager
{
	// --------------------------------------------------------------------------------------------------------------------
    // CONSTANTS
    // --------------------------------------------------------------------------------------------------------------------
	/**
	 * Identifies the document as a PDF file.
	 */
	public static final String PDF = "PDF";
	
	/**
	 * Identifies the document as a HTML webpage.
	 */
	public static final String HTML = "HTML";
	
    // --------------------------------------------------------------------------------------------------------------------
    // FIELDS
    // --------------------------------------------------------------------------------------------------------------------

    /**
     * The helper that receives the queries.
     */
    private SQLiteHelper helper;

    /**
     * The database that runs the queries.
     */
    private SQLiteDatabase database;

    // --------------------------------------------------------------------------------------------------------------------
    // METHODS
    // --------------------------------------------------------------------------------------------------------------------
    /**
     * Creates an instance of the manager.
     * @param context The activity that calls the manager.
     */
    public PersistenceManager( Context context )
    {
        helper = new SQLiteHelper( context );
    }

    /**
     * Creates a new document with the name given by the user.
     * @param url The name of the new mix.
     * @throws Exception If there is an error inserting the data.
     */
    public void createDocument( String url, String type, String title ) throws Exception
    {
        try
        {
            database = helper.getWritableDatabase( );
            ContentValues values = new ContentValues( );
            values.put( SQLiteHelper.COLUMN_DOCUMENT_TITLE, title);
            values.put( SQLiteHelper.COLUMN_DOCUMENT_URL, url );
            values.put( SQLiteHelper.COLUMN_DOCUMENT_TIPO, type);
            values.put(SQLiteHelper.COLUMN_DOCUMENT_SYNCED, 0);
            database.insert( SQLiteHelper.TABLE_DOCUMENTS, null, values );
            database.close( );
            values = null;
            database = null;
        }
        catch( Exception e )
        {
            throw new Exception( e.getMessage( ) );
        }
    }

    /**
     * Returns the id of the mix identified by the parameter.
     * @param mixName The name of the mix.
     * @return The id of the found mix. <b>-1</b> if the mix is not found.
     * @throws Exception If there is an error trying to find the mix.
     */
    /**
     * @param document
     * @return
     * @throws Exception
     */
    private int getDocument( String document ) throws Exception
    {
        int id = -1;
        try
        {
            database = helper.getReadableDatabase( );
            Cursor cursor = database.query( SQLiteHelper.TABLE_DOCUMENTS, new String[]{ SQLiteHelper.COLUMN_DOCUMENT_ID }, SQLiteHelper.COLUMN_DOCUMENT_URL + " LIKE '" + document + "'", null, null, null, null );
            if( cursor.moveToFirst( ) )
            {
                id = cursor.getInt( 0 );
            }
            cursor.close( );
            database.close( );
            cursor = null;
            database = null;
        }
        catch( Exception e )
        {
            throw new Exception( "Error: " + e );
        }
        return id;
    }
    
    private String getDocumentTitle( String document ) throws Exception
    {
        String title = null;
        try
        {
            database = helper.getReadableDatabase( );
            Cursor cursor = database.query( SQLiteHelper.TABLE_DOCUMENTS, new String[]{ SQLiteHelper.COLUMN_DOCUMENT_TITLE }, SQLiteHelper.COLUMN_DOCUMENT_URL + " LIKE '" + document + "'", null, null, null, null );
            if( cursor.moveToFirst( ) )
            {
                title = cursor.getString(0);
            }
            cursor.close( );
            database.close( );
            cursor = null;
            database = null;
        }
        catch( Exception e )
        {
            throw new Exception( "Error: " + e );
        }
        return title;
    }

    /**
     * Get sounds corresponding to a mix identified by the id given.
     * @param documentName The name of the document
     * @return The array of paths of the songs.
     * @throws Exception if there is a mistake in the SQL query.
     */
    public String[] getPalabraClaveByDocument( String documentName ) throws Exception
    {
        String[] palabras_clave = null;
        ArrayList<String> temp = new ArrayList<String>();
        try
        {
            database = helper.getReadableDatabase( );
            String sql = "SELECT p." + SQLiteHelper.COLUMN_PALABRA_CLAVE + " FROM " + SQLiteHelper.TABLE_PALABRAS_CLAVE + " p JOIN " + SQLiteHelper.TABLE_DOCUMENTS + " d ON " + SQLiteHelper.COLUMN_P_D_ID + " = " + SQLiteHelper.COLUMN_DOCUMENT_ID
                    + " WHERE d." + SQLiteHelper.COLUMN_DOCUMENT_URL + " LIKE '" + documentName + "'";
            Cursor cursor = database.rawQuery( sql, null );
            
            if( cursor.moveToFirst( ) )
            {            	
            	do
            	{
            		temp.add(cursor.getString(0));
            	}
            	while(cursor.moveToNext());               
            }
            palabras_clave = new String[temp.size()];
            palabras_clave = temp.toArray(palabras_clave);
            cursor.close( );
            database.close( );
            cursor = null;
            database = null;
        }
        catch( Exception e )
        {
            throw new Exception( e.getMessage( ) );
        }
        return palabras_clave;
    }

    /**
     * Get sounds corresponding to a mix identified by the id given.
     * @param documentName The name of the document
     * @return The array of paths of the songs.
     * @throws Exception if there is a mistake in the SQL query.
     */
    public String[] getRecommendedClaveByDocument( String documentName ) throws Exception
    {
        String[] recommendations = null;
        ArrayList<String> temp = new ArrayList<String>();
        try
        {
            database = helper.getReadableDatabase( );
            String sql = "SELECT p." + SQLiteHelper.COLUMN_RECOMMENDACION_URL+ ", d."+ SQLiteHelper.COLUMN_DOCUMENT_TITLE + " FROM " + SQLiteHelper.TABLE_RECOMMENDACION + " p JOIN " + SQLiteHelper.TABLE_DOCUMENTS + " d ON p." + SQLiteHelper.COLUMN_R_D_ID + " = d."
                    + SQLiteHelper.COLUMN_DOCUMENT_ID + " WHERE d." + SQLiteHelper.COLUMN_DOCUMENT_URL + " LIKE '" + documentName + "'";
            Cursor cursor = database.rawQuery( sql, null );
            System.out.println("COnsulta "+cursor.getCount() +":-------------------------------------"+sql);
            System.out.println();
            if( cursor.moveToFirst( ) )
            {
            	do
            	{
            		System.out.println("Cursor:-------------------------------------"+cursor.getString(1));
            		if(!cursor.getString(1).equals("") && cursor.getString(1) != null)
            		{
            			temp.add(cursor.getString(1));
            		}
            		else
            		{
            			temp.add(cursor.getString(0));
            		}
            		
            	}
            	while(cursor.moveToNext());               
            }
            recommendations = new String[temp.size()];
            recommendations = temp.toArray(recommendations);            
            cursor.close( );
            database.close( );
            cursor = null;
            database = null;
        }
        catch( Exception e )
        {
            throw new Exception( e.getMessage( ) );
        }
        return recommendations;
    }
    
    
    
    
   

    /**
     * Returns all the mixes in the database.
     * @return The array of names of the mixes. 
     * @throws Exception If there is a database error.
     */
    public String[] getAllDocuments( ) throws Exception
    {
        String[] docs = null;
        ArrayList<String> temp = new ArrayList<String>();
        try
        {
            database = helper.getReadableDatabase( );
            Cursor cursor = database.query( SQLiteHelper.TABLE_DOCUMENTS, new String[]{ SQLiteHelper.COLUMN_DOCUMENT_URL}, null, null, null, null, null );
            if( cursor.moveToFirst( ) )
            {            	
            	do
            	{
            		temp.add(cursor.getString(0));
            	}
            	while(cursor.moveToNext());               
            }
            docs = new String[temp.size()];
            docs = temp.toArray(docs);
            cursor.close( );
            database.close( );
            cursor = null;
            database = null;
        }
        catch( Exception e )
        {
            throw new Exception( e.getMessage( ) );
        }
        return docs;
    }
    
    /**
     * Checks if the file with the given name is already in the documents table.
     * @param fileName Name of the file to check.
     * @return <b>repeated</b>: <b><i>True</i></b> if it is in the table, on the contrary <b><i>False</i></b>
     */
    public boolean isFileInTable(String fileName)
    {
    	boolean repeated = false;
    	try 
    	{
    		database = helper.getReadableDatabase();
    		Cursor cursor = database.query( SQLiteHelper.TABLE_DOCUMENTS, new String[]{ SQLiteHelper.COLUMN_DOCUMENT_URL}, SQLiteHelper.COLUMN_DOCUMENT_URL + " LIKE '"+fileName+"' ", null, null, null, null );
    		repeated = cursor.moveToFirst();
    		cursor.close();
    		database.close();
		} 
    	catch (Exception e) 
    	{
			System.out.println(e.getMessage());
		}
    	
    	return repeated;
    }
    
    
    public boolean isPKInTable(String palabra, String url)
    {
    	
    	boolean repeated = false;
    	try 
    	{
    		int id = getDocument(url);
    		database = helper.getReadableDatabase();
    		Cursor cursor = database.query( SQLiteHelper.TABLE_PALABRAS_CLAVE, new String[]{ SQLiteHelper.COLUMN_PALABRA_CLAVE_ID}, SQLiteHelper.COLUMN_PALABRA_CLAVE+ " LIKE '"+palabra+"' AND "
    		+ SQLiteHelper.COLUMN_P_D_ID + " = "+id, null, null, null, null );
    		repeated = cursor.moveToFirst();
    		cursor.close();
    		database.close();
		} 
    	catch (Exception e) 
    	{
			System.out.println(e.getMessage());
		}
    	
    	return repeated;
    }
    
    public boolean isRecInTable(String rec, String url)
    {
    	
    	boolean repeated = false;
    	try 
    	{
    		int id = getDocument(url);
    		database = helper.getReadableDatabase();
    		Cursor cursor = database.query( SQLiteHelper.TABLE_RECOMMENDACION, new String[]{ SQLiteHelper.COLUMN_RECOMEN_ID}, SQLiteHelper.COLUMN_RECOMMENDACION_URL+ " LIKE '"+rec+"' AND "
    		+ SQLiteHelper.COLUMN_R_D_ID + " = "+id, null, null, null, null );
    		repeated = cursor.moveToFirst();
    		cursor.close();
    		database.close();
		} 
    	catch (Exception e) 
    	{
			System.out.println(e.getMessage());
		}
    	
    	return repeated;
    }
    
    public void savePalabrasClave(String document, String[] pk  )
    {
        try {           
            int id = getDocument(document);
            database = helper.getWritableDatabase();
            ContentValues values;
            for (int i = 0; i < pk.length; i++) {
                String rec = pk[i];
                values = new ContentValues();
                values.put(SQLiteHelper.COLUMN_PALABRA_CLAVE, rec);
                values.put(SQLiteHelper.COLUMN_R_D_ID, id);
                database.insert(SQLiteHelper.TABLE_PALABRAS_CLAVE, null,values);
                values = null;
            }           
            database.close();
            database = null;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void saveRecommendations(String document, String[] recs  )
    {
    	try {    		
			int id = getDocument(document);
			
			ContentValues values;
			System.out.println("Recs:                             "+Arrays.deepToString(recs));
			for (int i = 0; i < recs.length; i++) {
				String rec = recs[i];
				if(!isRecInTable(rec, document))
				{
					database = helper.getWritableDatabase();
					System.out.println(":::::::::::::::::::SAVE:::::::::::::::::::::");
					values = new ContentValues();
					values.put(SQLiteHelper.COLUMN_RECOMMENDACION_URL, rec);
					values.put(SQLiteHelper.COLUMN_R_D_ID, id);
					database.insert(SQLiteHelper.TABLE_RECOMMENDACION, null,values);
					values = null;
					database.close();
				}				
			}			
			
			database = null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public ArrayList<String> recomendar(String document)
    {    	
    	ArrayList<String> recomendaciones = null;
    	
    	try 
    	{
    		String[] documents = getAllDocuments();
			String[] p2 = getPalabraClaveByDocument(document);
			recomendaciones = new ArrayList<String>();
			for(int i = 0; i < documents.length; i ++)
			{
				if(!documents[i].equals(document))
				{
					String[] p = getPalabraClaveByDocument(documents[i]);
					if(p2!= null && compareArrays(p2, p))
					{
						recomendaciones.add(getDocumentTitle(documents[i]));
						String[] r = getRecommendedClaveByDocument(documents[i]);
						for(String s:r)
						{
							if(!recomendaciones.contains(s))
							{
								recomendaciones.add(s);
							}
						}
					}
				}
			}
			
			
		} 
    	catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return recomendaciones;
    }
    
    
    
    private boolean compareArrays(String[] arr1, String[] arr2)
    {
    	boolean equal = false;
    	for(int i = 0; i < arr1.length && !equal; i++)
    	{
    		for(int j = 0; j < arr2.length && !equal; j++)
        	{
        		equal = arr1[i].equalsIgnoreCase(arr2[j]);
        	}
    	}
    	return equal;
    }
    
    public String[] getUnsyncedPaths()
    {
    	String[] paths = null;
    	ArrayList<String> temp = new ArrayList<String>();
    	try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.query(SQLiteHelper.TABLE_DOCUMENTS, new String[]{SQLiteHelper.COLUMN_DOCUMENT_URL}, SQLiteHelper.COLUMN_DOCUMENT_SYNCED + " = 0", null, null, null, null);
			if(cursor.moveToFirst())
			{
				do
            	{
            		temp.add(cursor.getString(0));
            	}
            	while(cursor.moveToNext());
			}
			paths = new String[temp.size()];
            paths = temp.toArray(paths);
            cursor.close( );
            database.close( );
            cursor = null;
            database = null;
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();			
		}
    	return paths;
    }
    
    public void setSynced(String path)
    {
    	try {
    		int id = getDocument(path);
			database = helper.getWritableDatabase();
			ContentValues values = new ContentValues( );
			values.put(SQLiteHelper.COLUMN_DOCUMENT_SYNCED, 1);
			database.update(SQLiteHelper.TABLE_DOCUMENTS, values, SQLiteHelper.COLUMN_DOCUMENT_ID + " = "+ id , null);
		} catch (Exception e) {
			// TODO: handle exception
		}
    }
}