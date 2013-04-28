package com.data.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * Manages the creation and update of the phone's database.
 * @author Kelvin Guerrero
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
	protected static final String COLUMN_DOCUMENT_TITLE = "document_title";

	/**
	 * The name of the document, given by the user after saving the document.
	 */
	protected static final String COLUMN_DOCUMENT_URL = "document_name";

	   /**
     * The name of the document, given by the user after saving the document.
     */
    protected static final String COLUMN_DOCUMENT_TIPO= "document_tipo";

	
	//---------------------------------------------------------------------
	//Recomendaciones Table
	//---------------------------------------------------------------------
	/**
	 * The constant of the name of the recommendation table in the phone's database.
	 */
	protected static final String TABLE_RECOMMENDACION = "recommendation";
	
	/**
	 * The sound id in the sound table.
	 */
	protected static final String COLUMN_RECOMEN_ID = "recommendation_id";
	
	/**
	 * The complete Url of the recommendation. 
	 */
	protected static final String COLUMN_RECOMMENDACION_URL = "recommendation_url";
	
	/**
     * The recommendation referencing the document id.
     */
	protected static final String COLUMN_R_D_ID = "document_id";
	
	//---------------------------------------------------------------------
	//Palabras clave Table
	//---------------------------------------------------------------------
	/**
	 * The mixes and sound reference table in the database.
	 */
	protected static final String TABLE_PALABRAS_CLAVE = "palabras_clave";
	
	/**
	 * The palabra clave id in the table.
	 */
	protected static final String COLUMN_PALABRA_CLAVE_ID = "palabra_id";

	/**
	 * The palabra clave in the table.
	 */
	protected static final String COLUMN_PALABRA_CLAVE = "palabra";
	
	/**
	 * The palabra id referencing the document id.
	 */
	protected static final String COLUMN_P_D_ID = "document_id";
	
	//---------------------------------------------------------------------
	//Database Constants
	//---------------------------------------------------------------------
	/**
	 * The name of the database to create.
	 */
	private static final String DATABASE_NAME = "magik.db";
	
	/**
	 * The version of the database.
	 */
	private static final int DATABASE_VERSION = 1;

	/**
	 * The mixes table creation SQL statement.
	 */
	private static final String CREATE_DOCUMENT = "create table if not exists " + TABLE_DOCUMENTS + " (" + COLUMN_DOCUMENT_ID + " integer primary key autoincrement, "
			+ COLUMN_DOCUMENT_TITLE + " text, " + COLUMN_DOCUMENT_URL + " text, " + COLUMN_DOCUMENT_TIPO +" text not null )";
	/**
	 * The recommendation table creation SQL statement.
	 */
	private static final String CREATE_RECOMMENDATION = "create table if not exists " + TABLE_RECOMMENDACION + " (" + COLUMN_RECOMEN_ID
			+ " integer primary key autoincrement, " + COLUMN_RECOMMENDACION_URL + " text not null, " 
			+ COLUMN_R_D_ID + " integer " + ",FOREIGN KEY(" + COLUMN_R_D_ID + ") REFERENCES "
            + TABLE_DOCUMENTS + "(" + COLUMN_DOCUMENT_ID + "))";
	
	/**
     * The Palabras clave table creation SQL statement.
     */
    private static final String CREATE_PALABRAS_CLAVE = "create table if not exists " + TABLE_PALABRAS_CLAVE + " (" + COLUMN_PALABRA_CLAVE_ID
            + " integer primary key autoincrement, " + COLUMN_PALABRA_CLAVE + " text not null, " 
            + COLUMN_P_D_ID + " integer " + " ,FOREIGN KEY(" + COLUMN_P_D_ID + ") REFERENCES "
            + TABLE_DOCUMENTS + "(" + COLUMN_DOCUMENT_ID + "))";
            	
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

	/*
	 * (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_DOCUMENT);
		database.execSQL(CREATE_RECOMMENDATION);
		database.execSQL(CREATE_PALABRAS_CLAVE);
	}

	/*
	 * (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCUMENTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECOMMENDACION);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PALABRAS_CLAVE);
		onCreate(db);
	}

}