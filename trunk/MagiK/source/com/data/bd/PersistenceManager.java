package com.data.bd;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Creates queries to the SQLite helper and returns its results. 
 * @author Julio Mendoza
 *
 */
public class PersistenceManager {
	
	//--------------------------------------------------------------------------------------------------------------------
	//FIELDS
	//--------------------------------------------------------------------------------------------------------------------
	
	/**
	 * The helper that receives the queries.
	 */
	private SQLiteHelper helper;
	
	/**
	 * The database that runs the queries.
	 */
	private SQLiteDatabase database;
	
	//--------------------------------------------------------------------------------------------------------------------
	//METHODS
	//--------------------------------------------------------------------------------------------------------------------
	/**
	 * Creates an instance of the manager.
	 * @param context The activity that calls the manager.
	 */
	public PersistenceManager(Context context)
	{
		helper = new SQLiteHelper(context);
	}
	
	/**
	 * Creates a new mix with the name given by the user.
	 * @param documentName The name of the new mix.
	 * @throws Exception If there is an error inserting the data.
	 */
	public void createDocument(String documentName) throws Exception
	{
		try 
		{
			database = helper.getWritableDatabase();
			ContentValues values = new ContentValues();
	//		values.put(SQLiteHelper.COLUMN_MIX_NAME, documentName);			
//			database.insert(SQLiteHelper.TABLE_MIXES, null,values);
			database.close();
			values = null;
			database = null;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	/**
	 * Creates a sound in the database by the given file name.
	 * @param fileName The location of the file on the SD card.
	 * @param soundIndex 
	 * @param soundId 
	 * @throws Exception if there is an error inserting the data.
	 */
	private void createSound(String fileName, int soundId, int soundIndex) throws Exception
	{
		try 
		{
			database = helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(SQLiteHelper.COLUMN_SOUND_PATH, fileName);
			values.put(SQLiteHelper.COLUMN_SOUND_IDENT, soundId);
			values.put(SQLiteHelper.COLUMN_SOUND_INDEX, soundIndex);			
			database.insert(SQLiteHelper.TABLE_SOUNDS, null,values);
			database.close();
			values = null;
			database = null;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	/**
	 * Returns the id of the mix identified by the parameter. 
	 * @param mixName The name of the mix.
	 * @return The id of the found mix. <b>-1</b> if the mix is not found.
	 * @throws Exception If there is an error trying to find the mix.
	 */
	/**
	 * @param mixName
	 * @return
	 * @throws Exception
	 */
	private int getMix(String mixName) throws Exception
	{
		int id = -1;
		try {
			database = helper.getReadableDatabase();
		//	Cursor cursor = database.query(SQLiteHelper.TABLE_MIXES,new String[] {SQLiteHelper.COLUMN_MIX_ID},
			//		SQLiteHelper.COLUMN_MIX_NAME + " LIKE '"+ mixName+"'", null, null, null, null);
			//if(cursor.moveToFirst())
			//{
			//	id = cursor.getInt(0);
			//}			
			//cursor.close();
			//database.close();
			//cursor = null;
			database = null;
		} catch (Exception e) {
			throw new Exception("Error: "+e);
		}
		return id;
	}
	
	/**
	 * Returns the id of the sound identified by the parameter.
	 * @param path The path of the sound file to search.
	 * @param soundIndex 
	 * @param soundId 
	 * @return The id of the found mix. <b>-1</b> if the mix is not found.
	 * @throws Exception If there is an error trying to find the mix.
	 */
	private int getSound(String path, int soundId, int soundIndex) throws Exception
	{
		int id = -1;
		try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.query(SQLiteHelper.TABLE_SOUNDS,new String[] {SQLiteHelper.COLUMN_SOUND_ID},
					SQLiteHelper.COLUMN_SOUND_PATH + " LIKE '" + path + "' AND " + SQLiteHelper.COLUMN_SOUND_INDEX + " = " + soundIndex
					+ " AND " + SQLiteHelper.COLUMN_SOUND_IDENT + " = " + soundId, null, null, null, null);
			if(cursor.moveToFirst())
			{
				id = cursor.getInt(0);
			}			
			cursor.close();
			database.close();
			cursor = null;
			database = null;
		} catch (Exception e) {
			throw new Exception("Error: "+e);
		}
		return id;
	}
	
	/**
	 * Assigns a sound to a mix in the database.
	 * @param mixName The name of the mix to assign.
	 * @param path The path of the sound file.
	 * @throws Exception If there is an error trying to insert the values in the table.
	 */
	public void assignSoundToMix(String mixName, String path, int soundId, int soundIndex) throws Exception
	{
		try {
			int m_id = getMix(mixName);
			int s_id = getSound(path, soundId, soundIndex);
			if(m_id == -1)
			{
				createDocument(mixName);
				m_id = getMix(mixName);
			}
			if(s_id==-1)
			{
				createSound(path, soundId, soundIndex);
				s_id = getSound(path, soundId, soundIndex);
			}
			database = helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(SQLiteHelper.COLUMN_M_ID, m_id);
			values.put(SQLiteHelper.COLUMN_S_ID, s_id);
			database.insertWithOnConflict(SQLiteHelper.TABLE_M_S, null,values, SQLiteDatabase.CONFLICT_REPLACE);
			database.close();
			values = null;
			database = null;			
		}		
		catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		
	}
	
	/**
	 * Returns all the mixes in the database.
	 * @return The array of names of the mixes.
	 * @throws Exception If there is a database error.
	 */
	public String[] getAllMixes() throws Exception
	{
		String[] mixes = null;
		try {
			database = helper.getReadableDatabase();
//			Cursor cursor = database.query(SQLiteHelper.TABLE_MIXES,new String[] {SQLiteHelper.COLUMN_MIX_NAME},
//					null, null, null, null, null);
//			if(cursor.moveToFirst())
//			{
//				mixes = new String[cursor.getCount()];
//				for(int i = 0; i < mixes.length; i ++)
//				{
//					mixes[i] = cursor.getString(0); 
//				}
//			}			
//			cursor.close();
//			database.close();
//			cursor = null;
//			database = null;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return mixes;
	}
	
	/**
	 * Get sounds corresponding to a mix identified by the id given.
	 * @param mixId The id of the mix
	 * @return The array of paths of the songs.
	 * @throws Exception if there is a mistake in the SQL query.
	 */
//	public Sound[] getSoundsByMix(int mixId) throws Exception
//	{
//		Sound[] sounds = null;
//		try {
//			database = helper.getReadableDatabase();
//			String sql = "SELECT s."+SQLiteHelper.COLUMN_SOUND_IDENT+", s."+SQLiteHelper.COLUMN_SOUND_INDEX+", s."+SQLiteHelper.COLUMN_SOUND_PATH+" FROM "+
//			SQLiteHelper.TABLE_SOUNDS+" s JOIN (SELECT ms."+SQLiteHelper.COLUMN_S_ID+ " FROM " +SQLiteHelper.TABLE_M_S
//			+ " ms JOIN "+ SQLiteHelper.TABLE_MIXES
//						+" m ON ms."+SQLiteHelper.COLUMN_M_ID+" = m."+SQLiteHelper.COLUMN_MIX_ID+" WHERE m."+SQLiteHelper.COLUMN_MIX_ID
//						+"= "+mixId+") a ON s."+SQLiteHelper.COLUMN_SOUND_ID+"= a.s_id;";
//			System.out.println(sql);
//			Cursor cursor = database.rawQuery(sql, null);
//			if(cursor.moveToFirst())
//			{
//				sounds = new Sound[cursor.getCount()];
//				Sound s;
//				for(int i = 0; i < sounds.length; i ++)
//				{
//					s = new Sound(cursor.getInt(0), cursor.getInt(1), cursor.getString(2));
//					sounds[i] = s; 
//				}
//			}			
//			cursor.close();
//			database.close();
//			cursor = null;
//			database = null;
//		} catch (Exception e) {
//			throw new Exception(e.getMessage());
//		}
//		return sounds;
//	}
	
	/**
	 * Returns the mix id of amix given its name.
	 * @param name
	 * @return The id of the mix. -1 if not found.
	 */
//	public int getMixByName(String name)
//	{
//		int id = -1;
//		database = helper.getReadableDatabase();
//		Cursor cursor = database.query(SQLiteHelper.TABLE_MIXES, new String[] {SQLiteHelper.COLUMN_MIX_ID}, SQLiteHelper.COLUMN_MIX_NAME+ " LIKE '"+ name + "'", null, null, null, null);
//		if(cursor.moveToFirst())
//		{
//			id = cursor.getInt(0);
//		}
//		cursor.close();
//		database.close();
//		return id;
//	}
	
	/**
	 * Is true if the mix exists in the database.
	 * @param name The name of the database to search.
	 * @return <b>True</b> if the mix exists, <b>False</b> on the contrary.
	 * @throws Exception if there is an error looking the mix up.
	 */
	public boolean mixExists(String name) throws Exception
	{		
		int id = getMix(name);
		boolean exists = id != -1? true:false;
		return exists;
	}
	
	
	
	
}
