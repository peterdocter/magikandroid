package com.data.bd;

import java.util.ArrayList;

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
     * Creates a sound in the database by the given file name.
     * @param recommendet_url The location of the document in the WEB.
     * @param recommendet_document_id Id of the recommendation in the associated with the Document
     * @param reommendetId Id of the recommendation in the Data Base
     * @throws Exception if there is an error inserting the data.
     */
    private void createRecommendation( int reommendetId, String recommendet_url, int recommendet_document_id ) throws Exception
    {
        try
        {
            database = helper.getWritableDatabase( );
            ContentValues values = new ContentValues( );
            values.put( SQLiteHelper.COLUMN_RECOMMENDACION_URL, recommendet_url );
            values.put( SQLiteHelper.COLUMN_RECOMEN_ID, reommendetId );
            values.put( SQLiteHelper.COLUMN_R_D_ID, recommendet_document_id );
            database.insert( SQLiteHelper.TABLE_RECOMMENDACION, null, values );
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
     * Creates a sound in the database by the given file name.
     * @param palabra_clave The location of the document in the WEB.
     * @param palabra_document_id Id of the recommendation in the associated with the Document
     * @param palabraId Id of the recommendation in the Data Base
     * @throws Exception if there is an error inserting the data.
     */
    private void createPalabraClave( int palabraId, String palabra_clave, int palabra_document_id ) throws Exception
    {
        try
        {
            database = helper.getWritableDatabase( );
            ContentValues values = new ContentValues( );
            values.put( SQLiteHelper.COLUMN_PALABRA_CLAVE, palabra_clave );
            values.put( SQLiteHelper.COLUMN_PALABRA_CLAVE_ID, palabraId );
            values.put( SQLiteHelper.COLUMN_P_D_ID, palabra_document_id );
            database.insert( SQLiteHelper.TABLE_PALABRAS_CLAVE, null, values );
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
    private int getRecommendation( String recommendation ) throws Exception
    {
        int id = -1;
        try
        {
            database = helper.getReadableDatabase( );
            Cursor cursor = database.query( SQLiteHelper.TABLE_RECOMMENDACION, new String[]{ SQLiteHelper.COLUMN_RECOMEN_ID }, SQLiteHelper.COLUMN_RECOMMENDACION_URL + " LIKE '" + recommendation + "'", null, null, null, null );
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
    private int getPalabraClave( String palabraClave ) throws Exception
    {
        int id = -1;
        try
        {
            database = helper.getReadableDatabase( );
            Cursor cursor = database.query( SQLiteHelper.TABLE_PALABRAS_CLAVE, new String[]{ SQLiteHelper.COLUMN_PALABRA_CLAVE_ID }, SQLiteHelper.COLUMN_PALABRA_CLAVE + " LIKE '" + palabraClave + "'", null, null, null, null );
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

    /**
     * Get sounds corresponding to a mix identified by the id given.
     * @param documentName The name of the document
     * @return The array of paths of the songs.
     * @throws Exception if there is a mistake in the SQL query.
     */
    public String[] getPalabraClaveByDocument( String documentName ) throws Exception
    {
        String[] palabras_clave = null;
        try
        {
            database = helper.getReadableDatabase( );
            String sql = "SELECT p." + SQLiteHelper.COLUMN_PALABRA_CLAVE + " FROM " + SQLiteHelper.TABLE_PALABRAS_CLAVE + " p JOIN " + SQLiteHelper.TABLE_DOCUMENTS + " d ON " + SQLiteHelper.COLUMN_P_D_ID + " = " + SQLiteHelper.COLUMN_DOCUMENT_ID
                    + "WHERE d." + SQLiteHelper.COLUMN_DOCUMENT_URL + "LIKE '" + documentName + "'";
            System.out.println( sql );
            Cursor cursor = database.rawQuery( sql, null );
            if( cursor.moveToFirst( ) )
            {
                palabras_clave = new String[cursor.getCount( )];
                String s;
                for( int i = 0; i < palabras_clave.length; i++ )
                {
                    s = cursor.getString( 0 );
                    palabras_clave[ i ] = s;
                }
            }
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
        try
        {
            database = helper.getReadableDatabase( );
            String sql = "SELECT p." + SQLiteHelper.COLUMN_RECOMMENDACION_URL + " FROM " + SQLiteHelper.TABLE_RECOMMENDACION + " p JOIN " + SQLiteHelper.TABLE_DOCUMENTS + " d ON p." + SQLiteHelper.COLUMN_R_D_ID + " = d."
                    + SQLiteHelper.COLUMN_DOCUMENT_ID + " WHERE d." + SQLiteHelper.COLUMN_DOCUMENT_URL + " LIKE '" + documentName + "'";
            System.out.println( sql );
            Cursor cursor = database.rawQuery( sql, null );
            if( cursor.moveToFirst( ) )
            {
                recommendations = new String[cursor.getCount( )];
                String s;
                for( int i = 0; i < recommendations.length; i++ )
                {
                    s = cursor.getString( 0 );
                    recommendations[ i ] = s;
                }
            }
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
        String[] mixes = null;
       
        try
        {
            database = helper.getReadableDatabase( );
            Cursor cursor = database.query( SQLiteHelper.TABLE_DOCUMENTS, new String[]{ SQLiteHelper.COLUMN_DOCUMENT_ID, SQLiteHelper.COLUMN_DOCUMENT_URL, SQLiteHelper.COLUMN_DOCUMENT_TIPO , SQLiteHelper.COLUMN_DOCUMENT_TITLE }, null, null, null, null, null );
            if( cursor.moveToFirst( ) )
            {
                mixes = new String[cursor.getCount( )];
                for( int i = 0; i < mixes.length; i++ )
                {
                    mixes[ i ] = cursor.getString( 0 );
                }
            }
            cursor.close( );
            database.close( );
            cursor = null;
            database = null;
        }
        catch( Exception e )
        {
            throw new Exception( e.getMessage( ) );
        }
        return mixes;
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
			database = helper.getWritableDatabase();
			ContentValues values;
			for (int i = 0; i < recs.length; i++) {
				String rec = recs[i];
				values = new ContentValues();
				values.put(SQLiteHelper.COLUMN_RECOMMENDACION_URL, rec);
				values.put(SQLiteHelper.COLUMN_R_D_ID, id);
				database.insert(SQLiteHelper.TABLE_RECOMMENDACION, null,values);
				values = null;
			}			
			database.close();
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
			String[] recs = getRecommendedClaveByDocument(document);
			recomendaciones = new ArrayList<String>();
			for(int i = 0; i < documents.length; i ++)
			{
				if(!documents[i].equals(document))
				{
					String[] r = getRecommendedClaveByDocument(documents[i]);
					if(compareArrays(recs, r))
					{
						recomendaciones.add(documents[i]);						
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

    // /**
    // * Returns the id of the sound identified by the parameter.
    // * @param path The path of the sound file to search.
    // * @param soundIndex
    // * @param soundId
    // * @return The id of the found mix. <b>-1</b> if the mix is not found.
    // * @throws Exception If there is an error trying to find the mix.
    // */
    // private int getSound(String path, int soundId, int soundIndex) throws Exception
    // {
    // int id = -1;
    // try {
    // database = helper.getReadableDatabase();
    // Cursor cursor = database.query(SQLiteHelper.TABLE_RECOMMENDACION,new String[] {SQLiteHelper.COLUMN_RECOMEN_ID},
    // SQLiteHelper.COLUMN_RECOMMENDACION_URL + " LIKE '" + path + "' AND " + SQLiteHelper.COLUMN_SOUND_INDEX + " = " + soundIndex
    // + " AND " + SQLiteHelper.COLUMN_SOUND_IDENT + " = " + soundId, null, null, null, null);
    // if(cursor.moveToFirst())
    // {
    // id = cursor.getInt(0);
    // }
    // cursor.close();
    // database.close();
    // cursor = null;
    // database = null;
    // } catch (Exception e) {
    // throw new Exception("Error: "+e);
    // }
    // return id;
    // }
    //
    // /**
    // * Assigns a sound to a mix in the database.
    // * @param mixName The name of the mix to assign.
    // * @param path The path of the sound file.
    // * @throws Exception If there is an error trying to insert the values in the table.
    // */
    // public void assignSoundToMix(String mixName, String path, int soundId, int soundIndex) throws Exception
    // {
    // try {
    // int m_id = getMix(mixName);
    // int s_id = getSound(path, soundId, soundIndex);
    // if(m_id == -1)
    // {
    // createDocument(mixName);
    // m_id = getMix(mixName);
    // }
    // if(s_id==-1)
    // {
    // createSound(path, soundId, soundIndex);
    // s_id = getSound(path, soundId, soundIndex);
    // }
    // database = helper.getWritableDatabase();
    // ContentValues values = new ContentValues();
    // values.put(SQLiteHelper.COLUMN_M_ID, m_id);
    // values.put(SQLiteHelper.COLUMN_S_ID, s_id);
    // database.insertWithOnConflict(SQLiteHelper.TABLE_M_S, null,values, SQLiteDatabase.CONFLICT_REPLACE);
    // database.close();
    // values = null;
    // database = null;
    // }
    // catch (Exception e) {
    // throw new Exception(e.getMessage());
    // }
    //
    // }
    //
    // /**
    // * Returns all the mixes in the database.
    // * @return The array of names of the mixes.
    // * @throws Exception If there is a database error.
    // */
    // public String[] getAllMixes() throws Exception
    // {
    // String[] mixes = null;
    // try {
    // database = helper.getReadableDatabase();
    // // Cursor cursor = database.query(SQLiteHelper.TABLE_MIXES,new String[] {SQLiteHelper.COLUMN_MIX_NAME},
    // // null, null, null, null, null);
    // // if(cursor.moveToFirst())
    // // {
    // // mixes = new String[cursor.getCount()];
    // // for(int i = 0; i < mixes.length; i ++)
    // // {
    // // mixes[i] = cursor.getString(0);
    // // }
    // // }
    // // cursor.close();
    // // database.close();
    // // cursor = null;
    // // database = null;
    // } catch (Exception e) {
    // throw new Exception(e.getMessage());
    // }
    // return mixes;
    // }
    //
    //
    //
    // /**
    // * Returns the mix id of amix given its name.
    // * @param name
    // * @return The id of the mix. -1 if not found.
    // */
    // public int getMixByName(String name)
    // {
    // int id = -1;
    // database = helper.getReadableDatabase();
    // Cursor cursor = database.query(SQLiteHelper.TABLE_MIXES, new String[] {SQLiteHelper.COLUMN_MIX_ID}, SQLiteHelper.COLUMN_MIX_NAME+ " LIKE '"+ name + "'", null, null,
    // null, null);
    // if(cursor.moveToFirst())
    // {
    // id = cursor.getInt(0);
    // }
    // cursor.close();
    // database.close();
    // return id;
    // }
    //
    // /**
    // * Is true if the mix exists in the database.
    // * @param name The name of the database to search.
    // * @return <b>True</b> if the mix exists, <b>False</b> on the contrary.
    // * @throws Exception if there is an error looking the mix up.
    // */
    // public boolean mixExists(String name) throws Exception
    // {
    // int id = getMix(name);
    // boolean exists = id != -1? true:false;
    // return exists;
    // }

}