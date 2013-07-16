package com.zenithed.loaderz.provider;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contracts the application's resources by providing strong definition of each one of them.
 */
public class FeedsContract {
	
	public static final String AUTHORITY = "com.vegolath.loadersexample.provider";
	public static final Uri CONTENT_AUTHORITY = Uri.parse("content://" + AUTHORITY);
	
	/**
	 * Represents any queried operation performed with the {@link FeedsProvider}.
	 */
	public static final class Operation implements BaseColumns {
		
		/**
		 * Defines the name of the operations table in the database.
		 */
		static final String TABLE_NAME = "operations";
		
		/**
		 * The content:// style URI for this table, which requests a directory of operations.
		 */
		public static final Uri CONTENT_URI = CONTENT_AUTHORITY.buildUpon().appendPath(TABLE_NAME).build();
		
		/**
		 * The MIME type of the results when a operation ID is appended to CONTENT_URI, yielding a subdirectory of a single operation.
		 * </br>
		 * </br>
		 * Constant Value: "vnd.android.cursor.item/vnd.feeds.operation"
		 */
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.feeds.operation";
		
		/**
		 * The MIME type of the results from CONTENT_URI when a specific ID value is not provided, 
		 * and multiple operations may be returned.
		 * </br>
		 * </br>
		 * Constant Value: "vnd.android.cursor.dir/vnd.feeds.operation"
		 */
		public static final String CONTENT_TYPE =  ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.feeds.operation";
		
		/**
		 * Type: string representation of raw {@link java.net.URI}.
		 */
		public static final String URI = "uri";
		
		/**
		 * Type: string representation of {@link android.content.Intent}'s action.
		 */
		public static final String ACTION = "action";
		
		/**
		 * Timestamp of the last update of this operation.
		 * Type: integer.
		 */
		public static final String TIMESTAMP = "timestamp";
		
		/**
		 * Column for flagging the success / failure of the given operation.
		 * Values are defined in the {@link com.zenithed.loaderz.provider.FeedsContract.Operation.Status} interface.
		 * </br>
		 * Type: integer.
		 */
		public static final String STATUS = "status";
		
		public interface Status {
			
			public static final int SUCCESS = 1;
			
			public static final int FAIL = 0;
		}
		
	}
	
	
	/**
	 * Represents a raw item in the feeds.
	 */
	public static final class Entry implements BaseColumns {
		
		/**
		 * Defines the name of the entries table in the database.
		 */
		static final String TABLE_NAME = "entries";
		
		/**
		 * The content:// style URI for this table, which requests a directory of entries.
		 */
		public static final Uri CONTENT_URI = CONTENT_AUTHORITY.buildUpon().appendPath(TABLE_NAME).build();
		
		/**
		 * The MIME type of the results when a entry ID is appended to CONTENT_URI, yielding a subdirectory of a single entry.
		 * </br>
		 * </br>
		 * Constant Value: "vnd.android.cursor.item/vnd.feeds.entry"
		 */
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.feeds.entry";
		
		/**
		 * The MIME type of the results from CONTENT_URI when a specific ID value is not provided, and multiple entries may be returned.
		 * </br>
		 * </br>
		 * Constant Value: "vnd.android.cursor.dir/vnd.feeds.entry"
		 */
		public static final String CONTENT_TYPE =  ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.feeds.entry";
		
		/**
		 * Type: String.
		 */
		public static final String TITLE = "title";
		
		/**
		 * Type: String.
		 */
		public static final String LINK = "link";
		
		/**
		 * Type: String.
		 */
		public static final String PUBLISHED = "published";
		
		/**
		 * Type: String.
		 */
		public static final String UPDATED = "updated";
		
		/**
		 * Type: String.
		 */
		public static final String SUMMARY = "summary";
		
	}

	
}
