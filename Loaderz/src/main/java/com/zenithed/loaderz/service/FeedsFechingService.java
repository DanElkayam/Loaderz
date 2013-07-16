package com.zenithed.loaderz.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.zenithed.loaderz.R;
import com.zenithed.loaderz.io.EntriesHelper;
import com.zenithed.loaderz.io.OperationHelper;
import com.zenithed.loaderz.provider.FeedsContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FeedsFechingService extends IntentService {
	
	private static final String TAG = "FeedsFechingService";
	
	// TODO: export this to preferences.
	private static final int REFRESH_INTERVAL = 20 * 1000;

	public FeedsFechingService() {
		super(TAG);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		
		// TODO: make this dynamic.
		final Uri uri = FeedsContract.Entry.CONTENT_URI;
		final String action = intent.getAction();
		final String mimeType = intent.getType();
		
		if (action.equals(Intent.ACTION_SYNC)) {
			
			// performs sync for entries.
			if (mimeType.equals(FeedsContract.Entry.CONTENT_TYPE) ||
				mimeType.equals(FeedsContract.Entry.CONTENT_ITEM_TYPE)) {
				
				
				/*
				 * Checks for the given uri operation if we needs to sync.
				 */
				final Uri operationUri = FeedsContract.Operation.CONTENT_URI;
				final String selection = FeedsContract.Operation.URI + "=? AND " 
											+ FeedsContract.Operation.ACTION + "=? AND "
											+ "(" + FeedsContract.Operation.TIMESTAMP + "<? OR "
												  + FeedsContract.Operation.STATUS + "=" + FeedsContract.Operation.Status.FAIL
											+ ")";
				
				final String [] selectionArgs = {
						uri.toString(),
						action,
						String.valueOf(System.currentTimeMillis() - REFRESH_INTERVAL)
				};
				
				ContentResolver contentResolver = getContentResolver();
				Cursor cursor = contentResolver.query(operationUri, 
													null, 
													selection, 
													selectionArgs, 
													null);

				final boolean shouldSkipSync = cursor != null && !cursor.moveToFirst();
				if (shouldSkipSync) {
					Log.i(TAG, "The data is up todate, no need for syncing.");
					return;
				}
				
				Log.i(TAG, "Starting syncing data.");
				
				/*
				 * Gets new data and persists it in our beloved provider.
				 */
				final String url = getResources().getString(R.string.stackoverflow_url);
				boolean hasSuccess = false;
				
				try {
					contentResolver.bulkInsert(uri, executeGet(url, new EntriesHelper(getApplicationContext())));
					hasSuccess = true;
					
				} catch (IOException e) {
					e.printStackTrace();
					// does nothing. it will update the table with the failure flag for next try.
				}
				
				/*
				 * Updates the operations table with our last try.
				 */
				ContentValues contentValues = new ContentValues();
				contentValues.put(FeedsContract.Operation.TIMESTAMP, System.currentTimeMillis());
				contentValues.put(FeedsContract.Operation.STATUS, hasSuccess ? 
						FeedsContract.Operation.Status.SUCCESS : FeedsContract.Operation.Status.FAIL);
				contentValues.put(FeedsContract.Operation.URI, uri.toString());
				contentValues.put(FeedsContract.Operation.ACTION, action);
				
				contentResolver.update(operationUri, 
						contentValues, 
						selection, 
						selectionArgs);
				
				// updates any observer for operation state changed.
				contentResolver.notifyChange(operationUri, null);
				
				// updates any observer for the new entries.
				contentResolver.notifyChange(FeedsContract.Entry.CONTENT_URI, null);
			}
		}
	}

	// TODO: support full error codes awareness and better exception handling.
	private ContentValues [] executeGet(String urlString, OperationHelper operationHelper) throws IOException {
		
		Log.d(TAG, "Requesting URL: " + urlString);
        
		URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.connect();

        InputStream inputStream = urlConnection.getInputStream();
        String response = readInputStream(inputStream);
        Log.d(TAG, "HTTP response: " + response);
        
        inputStream.close();
        urlConnection.disconnect();
        
        return operationHelper.parse(response);
	}
	
    private static String readInputStream(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String responseLine;
        StringBuilder responseBuilder = new StringBuilder();
        while ((responseLine = bufferedReader.readLine()) != null) {
            responseBuilder.append(responseLine);
        }
        
        bufferedReader.close();
        
        return responseBuilder.toString();
    }
}
