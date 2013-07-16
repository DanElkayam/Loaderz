package com.zenithed.loaderz.provider;

import java.util.ArrayList;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class FeedsProvider extends ContentProvider {

	private static final String TAG = "FeedsProvider";
	
	/*
	 * URI Matcher indexes definitions.
	 */
	private static final int ROOT					 = 0;
	private static final int OPERATIONS_DIR			 = 1;
	private static final int OPERATIONS_ITEM		 = 2;
	private static final int FEED_ENTRIES_DIR		 = 3;
	private static final int FEED_ENTRIES_ITEM		 = 4;
	
	/*
	 * URI Matcher definitions.
	 */
	private static final UriMatcher sUriMatcher = new UriMatcher(ROOT);
	static {
		// indexes the operations segment.
		sUriMatcher.addURI(FeedsContract.AUTHORITY, 
				FeedsContract.Operation.TABLE_NAME, OPERATIONS_DIR);
		sUriMatcher.addURI(FeedsContract.AUTHORITY, 
				FeedsContract.Operation.TABLE_NAME + "/#", OPERATIONS_ITEM);
		
		// indexes the entries segment.
		sUriMatcher.addURI(FeedsContract.AUTHORITY, 
				FeedsContract.Entry.TABLE_NAME, FEED_ENTRIES_DIR);
		sUriMatcher.addURI(FeedsContract.AUTHORITY, 
				FeedsContract.Entry.TABLE_NAME + "/#", FEED_ENTRIES_ITEM);
	}
	
	private SQLiteOpenHelper mDataBaseHelper;
	
	@Override
	public boolean onCreate() {
		mDataBaseHelper = new FeedsDatabase(getContext());
		
		return true;
	}

	@Override
	public String getType(Uri uri) {
		switch(sUriMatcher.match(uri)) {
		
		case OPERATIONS_DIR:
			return FeedsContract.Operation.CONTENT_TYPE;
			
		case OPERATIONS_ITEM:
			return FeedsContract.Operation.CONTENT_ITEM_TYPE;
		
		case FEED_ENTRIES_DIR:
			return FeedsContract.Entry.CONTENT_TYPE;
			
		case FEED_ENTRIES_ITEM:
			return FeedsContract.Entry.CONTENT_ITEM_TYPE;
			
		default:
			throw new IllegalArgumentException("Unsupported URI: " 
									+ uri + " for provider: " + TAG);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		Log.d(TAG, "Quering for URI: " + uri.toString());
		
		final SQLiteDatabase dataBase = mDataBaseHelper.getReadableDatabase();
		final int match = sUriMatcher.match(uri);
		
		String table = null;
		
		switch (match) {
		
		case OPERATIONS_DIR:
		case OPERATIONS_ITEM:
			
			table = FeedsContract.Operation.TABLE_NAME;
			
			if (match == OPERATIONS_ITEM) {
				final String operationId = uri.getLastPathSegment();
				
				selection = FeedsContract.Operation._ID + "=?";
				selectionArgs = new String [] { operationId };
			}
			
			break;
		
		case FEED_ENTRIES_DIR:
		case FEED_ENTRIES_ITEM:
			
			table = FeedsContract.Entry.TABLE_NAME;
			
			if (match == FEED_ENTRIES_ITEM) {
				final String entryId = uri.getLastPathSegment();
				
				selection = FeedsContract.Entry._ID + "=?";
				selectionArgs = new String [] { entryId };
			}
			
			Intent syncService = new Intent();
			syncService.setAction(Intent.ACTION_SYNC);
			syncService.setType(FeedsContract.Entry.CONTENT_TYPE);
			getContext().startService(syncService);
			
			break;
		
		default:
			throw new IllegalArgumentException("Unsupported URI: " 
									+ uri + " for provider: " + TAG);
		}
		
		Cursor cursor =  dataBase.query(table, projection, selection, 
										selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		Log.d(TAG, "Inserting for URI: " + uri.toString());
		
		final SQLiteDatabase db = mDataBaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        
        String table = null;
        
        switch (match) {
        
        case OPERATIONS_DIR:
        	table = FeedsContract.Operation.TABLE_NAME;
        	break;
        
		case FEED_ENTRIES_DIR:
			table = FeedsContract.Entry.TABLE_NAME;
			break;
			
		default:
			throw new IllegalArgumentException("Unsupported URI: " 
					+ uri + " for provider: " + TAG);
		}
        
        final long rawId = db.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);
		return ContentUris.withAppendedId(uri, rawId);
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		
		Log.d(TAG, "Inserting (bulk) for URI: " + uri.toString());
		
		final int result = super.bulkInsert(uri, values);
		getContext().getContentResolver().notifyChange(uri, null);
		return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		
		Log.d(TAG, "Updating for URI: " + uri.toString());
		
		final SQLiteDatabase db = mDataBaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        
        int result = 0;
        String table = null;
        
        switch (match) {
        
        case OPERATIONS_DIR:
        case OPERATIONS_ITEM:
        	
        	table = FeedsContract.Operation.TABLE_NAME;
        	
        	if (match == OPERATIONS_ITEM) {
        		final String operationId = uri.getLastPathSegment();
    			
        		selection = FeedsContract.Operation._ID + "=?";
    			selectionArgs = new String [] { operationId };
        	}
		
			break;
        
        case FEED_ENTRIES_DIR:
		case FEED_ENTRIES_ITEM:
			
			table = FeedsContract.Entry.TABLE_NAME;
			
			if (match == FEED_ENTRIES_ITEM) {
				final String entryId = uri.getLastPathSegment();
				
				selection = FeedsContract.Entry._ID + "=?";
				selectionArgs = new String [] { entryId };
			}
			
			break;
			
		default:
			throw new IllegalArgumentException("Unsupported URI: " 
									+ uri + " for provider: " + TAG);
		}
        
        result = db.update(table, values, selection, selectionArgs);
        
        if (result > 0) {
        	getContext().getContentResolver().notifyChange(uri, null);
        }
        
        return result;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		
		Log.d(TAG, "Deleting for URI: " + uri.toString());
		
		SQLiteDatabase dataBase = mDataBaseHelper.getWritableDatabase();
		int result = 0;
		
		switch(sUriMatcher.match(uri)) {
		
		case OPERATIONS_DIR:
			result = dataBase.delete(FeedsContract.Operation.TABLE_NAME, selection, selectionArgs);
			break;
			
		case OPERATIONS_ITEM:
			final String operationId = uri.getLastPathSegment();
			
			selection = FeedsContract.Entry._ID + "=?";
			selectionArgs = new String [] { operationId };
		
			result = dataBase.delete(FeedsContract.Operation.TABLE_NAME, selection, selectionArgs);
			break;
		
		case FEED_ENTRIES_DIR:
			result = dataBase.delete(FeedsContract.Entry.TABLE_NAME, selection, selectionArgs);
			break;
			
		case FEED_ENTRIES_ITEM:
			final String entryId = uri.getLastPathSegment();
			
			selection = FeedsContract.Entry._ID + "=?";
			selectionArgs = new String [] { entryId };
		
			result = dataBase.delete(FeedsContract.Entry.TABLE_NAME, selection, selectionArgs);
			break;
			
		default:
			throw new IllegalArgumentException("Unsupported URI: " 
									+ uri + " for provider: " + TAG);
		}

		if (result > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		
		return result;
	}
	
    /**
     * Apply the given set of {@link android.content.ContentProviderOperation}, executing inside
     * a {@link android.database.sqlite.SQLiteDatabase} transaction. All changes will be rolled back if
     * any single one fails.
     */
    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mDataBaseHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }
    
}
