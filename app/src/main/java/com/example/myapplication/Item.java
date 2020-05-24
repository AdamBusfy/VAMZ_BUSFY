package com.example.myapplication;

import java.util.Comparator;

/**
 * Objekt item
 *
 *  <code>Item</code> obsahuje atributy a metody ktoreho specifikuju jednu poznamku v zozname
 */
public class Item  {

	private int image;
	private String menoPoznamky;
	private String popisPoznamky;

	/**
	 * Defaultny konstruktor pre Item
	 *
	 * @param imageResource reprezentuje obrazok priradeny poznamke
	 * @param meno reprezentuje nazov poznamky
	 * @param popis reprezentuje popis poznamky
	 */
	public Item(int imageResource, String meno, String popis) {
		this.image = imageResource;
		this.menoPoznamky = meno;
		this.popisPoznamky = popis;
	}

	/**
	 * Vrati imageResource
	 * @return image resource
	 */
	public int getImageResource() {
		return this.image;
	}

	/**
	 * Vrati meno resp. nazov poznamky
	 * @return meno poznamky
	 */
	public String getMenoPoznamky() {
		return this.menoPoznamky;
	}

	/**
	 * Vrati popis poznamky
	 * @return popis poznamky
	 */
	public String getPopisPoznamky() {
		return this.popisPoznamky;
	}

	/**
	 * Nastavi novi popis poznamky
	 * @param novyPopis novy popis poznamky
	 */
	public void setPopisPoznamky(String novyPopis) {
		this.popisPoznamky = novyPopis;
	}

	/**
	 * Staticka trieda Comparator, v ktorej sa porovnavaju objekty
	 */
	public static Comparator<Item> itemComparator = new Comparator<Item>() {
		/**
		 * 	Sluzi na porovnavanie dvoch objektov typu Item
		 *
		 *
		 * @param item1 prvy item na porovnavanie
		 * @param item2 druhy item na porovnavanie
		 * @return vrati zaporne cislo nulu alebo kladne cislo
		 */
		public int compare(Item item1, Item item2) {
			String menoPoznamky = item1.getMenoPoznamky().toUpperCase();
			String menoPoznamky2 = item2.getMenoPoznamky().toUpperCase();
			//vzostupne
			return menoPoznamky.compareTo(menoPoznamky2);
			//zostupne
			//return menoPoznamky2.compareTo(menoPoznamky);
		}
	};
}
