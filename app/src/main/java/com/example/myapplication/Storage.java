package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import java.util.ArrayList;

/**
 * Objekt Storage, predstavuje úložisko v ktorom sa uschovávajú aktuálne poznámky v aplikácií.
 *
 */
public class Storage {
	DatabaseHelper databaseHelper;

	/**
	 * inicializacia databaseHelpera na zaklade contextu
	 * @param context context
	 */
	public Storage(Context context) {
		databaseHelper = new DatabaseHelper(context);
	}

	/**
	 * Vytiahne z databazy informacie o aktualnych itemoch a naplni nimi list ktory vrati.
	 * @return arrayList itemov
	 */
	public ArrayList<Item> get() {
		ArrayList<Item> list = new ArrayList<>();
		Cursor res = databaseHelper.getAllData();
		if (res.getCount() == 0) {
			return list;
		}

		while (res.moveToNext()) {
			list.add(new Item(R.drawable.ic_event_note, res.getString(1), res.getString(2)));
		}
		return list;
	}

	/**
	 * Metoda ulozi poznamky do databazy.
	 *
	 * @param items aktualne vytvorene itemy resp. poznamky
	 */
	public void set(ArrayList<Item> items) {
		databaseHelper.deleteAllData();

		for (Item item : items) {
			databaseHelper.insertData(item.getMenoPoznamky(), item.getPopisPoznamky());
		}
	}

}
