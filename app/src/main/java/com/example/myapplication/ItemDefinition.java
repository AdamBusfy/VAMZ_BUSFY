package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

/**
 * Aktivita ItemDefinition
 *
 * <code>ItemDefinition</code> reprezentuje aktivitu v ktorej dokazeme menit text poznamky,
 *  obsahuje editText ktory vdaka listenerom meni aktualny stav popisu jednotlivych poznamok
 */
public class ItemDefinition extends AppCompatActivity {

	private Storage storage;

	/**
	 * Metoda onCreate, ktora sa zavola pri otvoreni ItemDefinition aktivity,
	 * inicializuje potrebne premenne a na editTextevytvori listener,
	 * ktory caka na zmenu a naslednu aktualizaciu textu.
	 * @param savedInstanceState zaloha
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_definition);
		this.storage = new Storage(this);

		Bundle extras = getIntent().getExtras();
		final int itemIndex = Integer.parseInt(extras.getString("selected_item_index"), 10);

		final ArrayList<Item> items = this.storage.get();

		final Item item = items.get(itemIndex);

		final EditText editText = findViewById(R.id.editTextPoznamky);

		editText.setText(item.getPopisPoznamky());

		final Storage st = this.storage;

		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				EditText editText = findViewById(R.id.editTextPoznamky);
				item.setPopisPoznamky(editText.getText().toString());
				st.set(items);
			}

			@Override
			public void afterTextChanged(Editable s) {
				EditText editText = findViewById(R.id.editTextPoznamky);
				item.setPopisPoznamky(editText.getText().toString());
				st.set(items);

				Intent resultIntent = new Intent();
				setResult(RESULT_OK, resultIntent);
			}
		});
	}
}




