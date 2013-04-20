package com.data.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * Manages the creation and update of the phone's database.
 * @author Julio Mendoza
 *
 */
public class SQLiteHelper extends SQLiteOpenHelper {

	//---------------------------------------------------------------------
	//Documents Table
	//---------------------------------------------------------------------
	/**
	 * The constant of the name of the document table in the phone's database.
	 */
	protected static final String TABLE_DOCUMENTS = "documents";
	
	/**
	 * The id of the document in the mix table.
	 */
	protected static final String COLUMN_DOCUMENT_ID = "document_id";
	
	/**
	 * The name of the document, given by the user after saving the document.
	 */
	protected static final String COLUMN_DOCUMENT_TITLE= "document_title";

	//---------------------------------------------------------------------
	//Sounds Table
	//---------------------------------------------------------------------
	/**
	 * The constant of the name of the sounds table in the phone's database.
	 */
	protected static final String TABLE_SOUNDS = "sounds";
	
	/**
	 * The sound id in the sound table.
	 */
	protected static final String COLUMN_SOUND_ID = "sound_id";
	
	/**
	 * The complete path in the SD card of the sound in the mix. 
	 */
	protected static final String COLUMN_SOUND_PATH = "sound_path";
	
	/**
	 * The complete path in the SD card of the sound in the mix. 
	 */
	protected static final String COLUMN_SOUND_IDENT = "sound_ident";
	
	/**
	 * The complete path in the SD card of the sound in the mix. 
	 */
	protected static final String COLUMN_SOUND_INDEX = "sound_index";	
	
	
	//---------------------------------------------------------------------
	//Mixes_Sounds Table
	//---------------------------------------------------------------------
	/**
	 * The mixes and sound reference table in the database.
	 */
	protected static final String TABLE_M_S = "mixes_sounds";
	
	/**
	 * The mix_sound id in the table.
	 */
	protected static final String COLUMN_MS_ID = "ms_id";
	
	/**
	 * The sound id referencing the sound table's sound id.
	 */
	protected static final String COLUMN_S_ID = "s_id";
	
	/**
	 * The mix id referencing the mix table's mix id.
	 */
	protected static final String COLUMN_M_ID = "m_id";
	
	//---------------------------------------------------------------------
	//Database Constants
	//---------------------------------------------------------------------
	/**
	 * The name of the database to create.
	 */
	private static final String DATABASE_NAME = "socialmixer.db";
	
	/**
	 * The version of the database.
	 */
	private static final int DATABASE_VERSION = 1;

	/**
	 * The mixes table creation SQL statement.
	 */
//	private static final String CREATE_MIXES = "create table if not exists " + TABLE_MIXES + " (" + COLUMN_MIX_ID + " integer primary key autoincrement, "
//			+ COLUMN_MIX_NAME + " text not null)";
	/**
	 * The sounds table creation SQL statement.
	 */
	private static final String CREATE_SOUNDS = "create table if not exists " + TABLE_SOUNDS + " (" + COLUMN_SOUND_ID
			+ " integer primary key autoincrement, " + COLUMN_SOUND_IDENT
			+ " integer not null, " + COLUMN_SOUND_INDEX + " integer not null, " +
			COLUMN_SOUND_PATH + " text not null )";
	
	/**
	 * The mixes and sounds bridge table creation SQL statement.
	 */
//	private static final String CREATE_S_M = "create table if not exists " + TABLE_M_S + " (" + COLUMN_MS_ID
//			+ " integer primary key autoincrement, "+COLUMN_M_ID+ " integer unique, "+COLUMN_S_ID+ " integer unique, " + "FOREIGN KEY("
//			+ COLUMN_M_ID + ") REFERENCES " + TABLE_MIXES + "(" + COLUMN_MIX_ID
//			+ "), " + "FOREIGN KEY(" + COLUMN_S_ID + ") REFERENCES "
//			+ TABLE_SOUNDS + "(" + COLUMN_SOUND_ID + "))";
	
	
	//----------------------------------------------------------------------------
	//METHODS
	//----------------------------------------------------------------------------
	/**
	 * Initializes the helper.
	 * @param context
	 */
	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

    @Override
    public void onCreate( SQLiteDatabase db )
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion )
    {
        // TODO Auto-generated method stub
        
    }

	/*
	 * (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
//	@Override
//	public void onCreate(SQLiteDatabase database) {
//		database.execSQL(CREATE_MIXES);
//		database.execSQL(CREATE_SOUNDS);
//		database.execSQL(CREATE_S_M);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
//	 */
//	@Override
//	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		Log.w(SQLiteHelper.class.getName(),
//				"Upgrading database from version " + oldVersion + " to "
//						+ newVersion + ", which will destroy all old data");
//		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MIXES);
//		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOUNDS);
//		db.execSQL("DROP TABLE IF EXISTS " + TABLE_M_S);
//		onCreate(db);
//	}

}