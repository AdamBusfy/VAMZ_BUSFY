package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DatabaseHelper
 * <code>DatabaseHelper</code> extenduje SQLiteOpenHelper a reprezentuje triedu na pracu s SQLite databazov
 * stara sa o otvorenie databazy a komunikaciu s nou.
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	 /** Meno databazy */
	public static final String DATABASE_NAME = "Items.db";
	/** Nazov tabulky */
	public static final String TABLE_NAME = "item_table";
	/** Stlpec pre primarny kluc ID */
	public static final String COL_1 = "ID";
	/** Stlpec pre nazov poznamky */
	public static final String COL_2 = "NAME";
	/** Stlpec pre popis poznamky */
	public static final String COL_3 = "DESCRIPTION";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, 1);
	}

	/**
	 * Zavola sa ked sa databaza prvy krat vytvara, tu sa specifikuje nazov tabulky a jej atributov
	 * @param db db
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,DESCRIPTION TEXT)");
	}

	/**
	 * Zavola sa ked sa upgraduje cela databaza
	 *
	 * @param db databaza
	 * @param oldVersion stara verzia databazy
	 * @param newVersion nova verzia databazy
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	/**
	 * Metoda, ktora vlozi data do tabulky
	 *
	 * @param nazov nazov poznamky
	 * @param popis popis poznamky
	 * @return informaciu ci sa podarilo vlozit alebo nie
	 */
	public boolean insertData(String nazov, String popis) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(COL_2, nazov);
		contentValues.put(COL_3, popis);
		long result = db.insert(TABLE_NAME, null, contentValues);
		if (result == -1)
			return false;
		else
			return true;
	}

	/**
	 * Metoda, ktora vytiahne vsetky data z databazy
	 *
	 * @return Kurzor vytvoreny selektom nad tabulkami
	 */
	public Cursor getAllData() {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
		return res;
	}

	/**
	 * Metoda na update jednej poznamky
	 *
	 *
	 * @param id id poznamky
	 * @param nazov nazov poznamky
	 * @param popis popis poznamky
	 * @return informaciu ci sa podarilo update vykonat
	 */
	public boolean updateData(String id, String nazov, String popis) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(COL_1, id);
		contentValues.put(COL_2, nazov);
		contentValues.put(COL_3, popis);
		db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{id});
		return true;
	}

	/**
	 * Metoda na zmazanie konkretnej poznamky
	 *
	 * @param id id poznamky
	 * @return id deletnuteho itemu
	 */
	public Integer deleteData(String id) {
		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete(TABLE_NAME, "ID = ?", new String[]{id});
	}

	/**
	 * Metoda, ktora zmaze vsetky udaje z tabulky
	 *
	 */
	public void deleteAllData() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, null, null);
	}
}
