package com.example.myapplication;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

/**
 * WidgetProvider
 *
 * <code>WidgetProvider</code> provider, ktory sa stara o spravu widgetov
 */
public class WidgetProvider extends AppWidgetProvider {

	public static final String REFRESH = "refresh";

	/**
	 * Zavola sa na vyziadanie providera
	 *
	 * @param context aktualny kontext
	 * @param appWidgetManager objekt ktory sa da volat
	 * @param appWidgetIds Id widgetu ktory chceme aktualizovat
	 */
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		for (int appWidgetId : appWidgetIds) {

			Intent serviceIntent = new Intent(context, WidgetService.class);
			serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));


			Intent clickIntent = new Intent(context, WidgetProvider.class);
			clickIntent.setAction(REFRESH);
			PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context, 0 , clickIntent, 0);

			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
			views.setRemoteAdapter(R.id.stack_view, serviceIntent);
			views.setEmptyView(R.id.stack_view, R.id.empty_view);
			views.setPendingIntentTemplate(R.id.stack_view, clickPendingIntent);

			appWidgetManager.updateAppWidget(appWidgetId, views);
			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stack_view);
		}
	}

	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}

	/**
	 * Dokaze zavolat ine metody providera
	 *
	 * @param context aktualny kontext
	 * @param intent intent
	 */
	@Override
	public void onReceive(Context context, Intent intent) {

		if (REFRESH.equals(intent.getAction())) {
			int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stack_view);
		}
		super.onReceive(context, intent);
	}

	@Override
	public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
		super.onRestored(context, oldWidgetIds, newWidgetIds);
	}
}
