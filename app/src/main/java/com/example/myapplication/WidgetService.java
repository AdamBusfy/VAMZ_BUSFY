package com.example.myapplication;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import java.util.ArrayList;

/**
 * Služba, ku ktorej sa má pripojiť vzdialený adaptér, aby si vyžiadala RemoteViews.
 */
public class WidgetService extends RemoteViewsService {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new WidgetItemFactory(getApplicationContext(), intent);
	}

	class WidgetItemFactory implements RemoteViewsFactory {
		private Context context;
		private int appWidgetId;

		Storage storage = new Storage(getApplicationContext());

		private ArrayList<Item> items = storage.get();

		WidgetItemFactory(Context context, Intent intent) {
			this.context = context;
			this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		@Override
		public void onCreate() {
		}

		/**
		 * Vola sa ked sa zaznamena zmena
		 */
		@Override
		public void onDataSetChanged() {
			//System.out.println("MENIM DATA);
			items = storage.get();
		}

		@Override
		public void onDestroy() {
			//System.out.println("Zatvaaram");
		}

		@Override
		public int getCount() {
			return items.size();
		}

		/**
		 *
		 * @param position pozicia
		 * @return RemoteViews
		 */
		@Override
		public RemoteViews getViewAt(int position) {
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_item);
			views.setTextViewText(R.id.widget_item_text, items.get(position).getPopisPoznamky());

			Intent fillIntent = new Intent();
			fillIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			views.setOnClickFillInIntent(R.id.widget_item_text, fillIntent);
			return views;
		}

		@Override
		public RemoteViews getLoadingView() {
			//System.out.println("nacitavam");
			return null;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}
	}
}
