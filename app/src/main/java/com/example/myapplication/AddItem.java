package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Trieda AddItem
 *
 * <code>AddItem</code> reprezentuje aktivitu, ktora sa spusti po stlaceni button-a na pridanie poznamky
 *
 */
public class AddItem extends AppCompatActivity {

	/**
	 * Zavola sa pri vytvoreni, aktivity nastavi title a inicializuje potvrdzovaci button.
	 * @param savedInstanceState stav
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_item);
		setTitle("Pridaj kartu");

		findWeather();

		Button buttonAdd = findViewById(R.id.pridajTlacidlo);

		buttonAdd.setOnClickListener(new View.OnClickListener() {
			/**
			 * Skontroluje ci je vyplnene policko pre nazov ak ano vytvori resultIntent,
			 * v ktorom uschova potrebne informacie, nastavi result a finishne aktivitu.
			 * V opacnom pripade vytvori SnackBar, ktory informuje uzivatela o nevyplneni potrebnych udajov.
			 * @param v view
			 */
			@Override
			public void onClick(View v) {
				EditText editTextTitle = findViewById(R.id.nazov);
				EditText editTextPopis = findViewById(R.id.popisPoznamky);

				String title = editTextTitle.getText().toString();
				String popis = editTextPopis.getText().toString();

				if (TextUtils.isEmpty(title)) {
					Snackbar.make(v, "Nevyplnili ste názov poznámky.", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
				} else {
					Intent resultIntent = new Intent();
					resultIntent.putExtra("Title", title);
					resultIntent.putExtra("Popis", popis);

					setResult(RESULT_OK, resultIntent);
					finish();
				}
			}
		});
	}

	/**
	 * Metoda findWeather posle API request na server openweather pomocou mojho apiKluca,
	 *
	 */
	public void findWeather() {

		String url = "https://api.openweathermap.org/data/2.5/weather?q=Bytča&appid=616a7810260c8f1c137b4e1b3d27d96a&units=metric&lang=sk"; //units a lang specifikuju mernu jednotku a jazyk

		final TextView nazovMesta = findViewById(R.id.mesto);
		final TextView teplota = findViewById(R.id.teplota);
		final TextView pocitovaTeplota = findViewById(R.id.pocitovaTeplota);
		final TextView popis = findViewById(R.id.popis);

		JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
			/**
			 * Spracuje odpoved, ktora je v JSON formate a inicializuje potrebne premenne a nasledne nastavi
			 * hodnoty prislusnym TextViews, v opacnom pripade sa informacie nezobrazia.
			 *
			 * @param response odpoved
			 */
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject mainObject = response.getJSONObject("main");
					JSONArray array = response.getJSONArray("weather");
					JSONObject object = array.getJSONObject(0);
					String temp = String.valueOf(mainObject.getDouble("temp"));
					String feels_like = String.valueOf(mainObject.getDouble("feels_like"));
					String description = object.getString("description");
					String city = response.getString("name");

					nazovMesta.setText(city);
					teplota.setText(temp + "°C");
					pocitovaTeplota.setText(feels_like + "°C");

					description = description.substring(0, 1).toUpperCase() + description.substring(1);
					popis.setText(" " + description);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		}
		);

		RequestQueue queue = Volley.newRequestQueue(this);
		queue.add(jor);
	}
}
