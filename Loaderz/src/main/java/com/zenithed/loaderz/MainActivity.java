package com.zenithed.loaderz;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import com.zenithed.loaderz.provider.FeedsContract;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getFragmentManager().beginTransaction().replace(R.id.fragment_container, new FeedsFragment()).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	public static final class FeedsFragment extends ListFragment {
		
		public FeedsFragment() {}
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			setListAdapter(new SimpleCursorAdapter(
					getActivity(),
					android.R.layout.simple_list_item_1,
					null,
					new String[] {FeedsContract.Entry.TITLE },
					new int [] { android.R.id.text1},
					CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
			
			getActivity().getLoaderManager().initLoader(0, null, new ListItemLoaderCallbacks(this));
			
		}
		
		private static class ListItemLoaderCallbacks implements LoaderCallbacks<Cursor> {
			private final WeakReference<ListFragment> fragmet;

			public ListItemLoaderCallbacks(ListFragment fragmentInstance) {
				fragmet = new WeakReference<ListFragment>(fragmentInstance);
			}
			@Override
			public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
				CursorLoader loader = new CursorLoader(fragmet.get().getActivity());
				loader.setUri(FeedsContract.Entry.CONTENT_URI);
				loader.setProjection(new String[] { 
						FeedsContract.Entry._ID,
						FeedsContract.Entry.TITLE
				});
				return loader;
			}

			
			@Override
			public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
				((CursorAdapter)fragmet.get().getListAdapter()).changeCursor(cursor);
			}
			
			@Override
			public void onLoaderReset(Loader<Cursor> loader) {}
			
		}
		
	}

}