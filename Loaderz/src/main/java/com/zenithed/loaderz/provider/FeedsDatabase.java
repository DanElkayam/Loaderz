package com.zenithed.loaderz.provider;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * Manages the application SQLite database lifecycle.
 */
class FeedsDatabase extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "feeds-db";
	public static final int DATABASE_VERSION = 1;
	
	public FeedsDatabase(Context context) {
		this(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public FeedsDatabase(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	@Override
	public String getDatabaseName() {
		return DATABASE_NAME;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/*
		 * Creates the operations table.
		 */
		db.execSQL(
				"CREATE TABLE " + FeedsContract.Operation.TABLE_NAME
				+ "("
				+ FeedsContract.Operation._ID + " INTEGER PRIMARY KEY, "
				+ FeedsContract.Operation.URI + " TEXT, "
				+ FeedsContract.Operation.ACTION + " TEXT, "
				+ FeedsContract.Operation.TIMESTAMP + " INTEGER, "
				+ FeedsContract.Operation.STATUS + " INTEGER "
				+ ")"
		);
		
		/*
		 * Populates the table with supported operations.
		 */
		final ContentValues entriesSyncValues = new ContentValues();
		entriesSyncValues.put(FeedsContract.Operation.URI, FeedsContract.Entry.CONTENT_URI.toString());
		entriesSyncValues.put(FeedsContract.Operation.ACTION, Intent.ACTION_SYNC);
		entriesSyncValues.put(FeedsContract.Operation.TIMESTAMP, 0);
		entriesSyncValues.put(FeedsContract.Operation.STATUS, FeedsContract.Operation.Status.FAIL);
		db.insert(FeedsContract.Operation.TABLE_NAME, null, entriesSyncValues);
		
		/*
		 * Creates the entries table.
		 */
		db.execSQL(
				"CREATE TABLE " + FeedsContract.Entry.TABLE_NAME
				+ "("
				+ FeedsContract.Entry._ID + " INTEGER PRIMARY KEY, "
				+ FeedsContract.Entry.TITLE + " TEXT, "
				+ FeedsContract.Entry.LINK + " TEXT, "
				+ FeedsContract.Entry.PUBLISHED + " TEXT, "
				+ FeedsContract.Entry.UPDATED + " TEXT, "
				+ FeedsContract.Entry.SUMMARY + " TEXT "
				+ ")"
		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/*
		 * Removes the operations table.
		 */
		db.execSQL("DROP TABLE IF EXISTS " + FeedsContract.Operation.TABLE_NAME);
		
		/*
		 * Removes the feeds table.
		 */
		db.execSQL("DROP TABLE IF EXISTS " + FeedsContract.Entry.TABLE_NAME);
	}

}
