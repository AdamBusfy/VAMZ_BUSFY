package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Collections;


/**
 * MainActivity, jedna sa o zakladnu aktivitu celej aplikacie na ktorej su zobrazene jednotlive poznamky
 * nachadza sa tu tlacidlo na pridavanie poznamky a vyhladavaci SearchView na hladanie poznamok
 *
 *
 * @author Adam Busfy
 * @version 1.0.0 Maj 24, 2020
 */
public class MainActivity extends AppCompatActivity implements Adapter.OnItemListener {

	private RecyclerView recyclerView;
	private Adapter adapter;
	private ArrayList<Item> list;
	private Storage storage;
	private ShakeListener mShaker;

	/**
	 * Inicializacia listu
	 */
	public MainActivity(){
		this.list = new ArrayList<>();
	}

	/**
	 * Zavola sa po spusteni aplikacie, inicializuju sa potrebne atributy a nacitaju sa udaje z databazy
	 *
	 * @param savedInstanceState stav aplikacie
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mShaker = new ShakeListener(this);
		shake();

		this.storage = new Storage(this);

		loadPreferences();
		setLayout();

		initFloatingButton();
		initItemTouchHelper();
	}

	/**
	 * Metoda, kotra inicializuje floating buttons, nastavi im animacie osetri ich vykreslovanie
	 * a zabezpeci ich funkciu
	 */
	private void initFloatingButton() {
		final FloatingActionButton fab = findViewById(R.id.fab);
		final FloatingActionButton fabDelete = findViewById(R.id.delete_fab);
		final Animation show_anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.show_button);
		final Animation hide_anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.hide_button);
		final Animation rotate_in = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate_in);
		final Animation rotate_back = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate_back);
		final ArrayList<Item> myList = this.list;

		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (fabDelete.getVisibility() == View.VISIBLE) {
					fabDelete.setVisibility(view.GONE);
					fabDelete.setClickable(false);
					fabDelete.startAnimation(hide_anim);
					fab.startAnimation(rotate_back);
					fab.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.addButtonColor));

				} else {
					Intent intent = new Intent(MainActivity.this, AddItem.class);
					startActivityForResult(intent, 1);
				}
			}
		});

		fab.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {

				if (fabDelete.getVisibility() == View.GONE) {
					fabDelete.setVisibility(v.VISIBLE);
					fabDelete.setClickable(true);
					fabDelete.startAnimation(show_anim);
					fab.startAnimation(rotate_in);
					fab.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.grey));
				}
				return true;
			}
		});

		fabDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (fabDelete.getVisibility() == View.VISIBLE) {
					new AlertDialog.Builder(MainActivity.this)
						.setIcon(R.drawable.ic_warning)
						.setTitle("Ste si istý?")
						.setMessage("Naozaj chcete zmazať všetky poznámky?")
						.setPositiveButton("Áno", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (myList != null) {
									myList.clear();
									adapter.notifyDataSetChanged();
									fabDelete.setVisibility(View.GONE);
									fabDelete.setClickable(false);
									fabDelete.startAnimation(hide_anim);
									fab.startAnimation(rotate_back);
									SavePreferences();
								}
								fab.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.addButtonColor));
							}
						})
						.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								setLayout();
								fabDelete.setVisibility(View.GONE);
								fabDelete.setClickable(false);
								fabDelete.startAnimation(hide_anim);
								fab.startAnimation(rotate_back);
								fab.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.addButtonColor));
							}
						})
						.show();
				}
			}
		});
	}

	/**
	 * Metoda na inicializovanie TouchHelpera, ktory monitoruje jednotlive itemy
	 * a na zaklade akcie ktora sa na nich vykoava dalej smeruje chod aplikacie
	 */
	private void initItemTouchHelper() {
		ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
			/**
			 * Zabezpeci presun itemu
			 *
			 * @param recyclerView recyclerView ktory sleduje
			 * @param dragged tahany item
			 * @param target targetovany item
			 * @return vrati info o tom ci sa operacia vykonala
			 */
			@Override
			public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {

				int position_dragged = dragged.getAdapterPosition();
				int position_target = target.getAdapterPosition();

				Collections.swap(list, position_dragged, position_target);
				adapter.notifyItemMoved(position_dragged, position_target);
				SavePreferences();
				return false;
			}

			/**
			 * Vykresli alert dialog, v ktorom sa spyta na vykonanie odstranenia itemu po jeho
			 * swajpnuti do lava alebo do prava.
			 *
			 * @param viewHolder konkretny viewHolder itemu
			 * @param direction smer v ktorom sa viewHolder swajpuje
			 */
			@Override
			public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
				final int position = viewHolder.getAdapterPosition();

				new AlertDialog.Builder(MainActivity.this)
					.setIcon(R.drawable.ic_warning)
					.setTitle("Ste si istý?")
					.setMessage("Naozaj chcete zmazať poznámku?")
					.setPositiveButton("Áno", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							list.remove(position);
							adapter.notifyDataSetChanged();
							adapter.notifyItemRemoved(position);
							SavePreferences();
						}
					})
					.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							setLayout();
						}
					})
					.show();
			}

			/**
			 * metoda ktora sa zavola po vykonani interakcie s viewHolderom
			 * @param recyclerView recyclerView
			 * @param viewHolder viewHolder
			 */
			@Override
			public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
				super.clearView(recyclerView, viewHolder);
				viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.defaultWhite));
			}

			/**
			 * Zavola sa ked sa swajpuje alebo pohybuje s viewHolderom
			 *
			 * @param viewHolder viewHolder ktory sa bud swajpuje alebo snim pohybujeme
			 * @param actionState aktualna akcia ktora sa na viewHolderi vykonava
			 */
			@Override
			public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
				super.onSelectedChanged(viewHolder, actionState);
				if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
					viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
				}
			}
		});
		itemTouchHelper.attachToRecyclerView(recyclerView);
	}

	/**
	 * zabezpeci usporiadanie listu abecedne od A po Z  podla nazvu poznamky pri zatraseni telefonom
	 */
	private void shake() {
		//final ArrayList<Item> myList = this.list;
		mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
			public void onShake() {
				if (!list.isEmpty()) {
					Collections.sort(list, Item.itemComparator);
					Toast.makeText(MainActivity.this, "Zoradené (A-Z)", Toast.LENGTH_LONG).show();
					SavePreferences();
					loadPreferences();
					setLayout();
				}
			}
		});
	}

	/**
	 * Ak sa aktivita ktora zacala ako startActivityForResult(), ukonci zavola sa tato metoda ktora overi
	 * result kod a na jeho zaklade vykona dalsie operacie
	 * @param requestCode kod s ktorym zacala aktivita
	 * @param resultCode kod ktory sa vratil ako vysledok aktivity
	 * @param data intent v kotrom vieme vratiti potrebne data
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 1) {
			String nazov = data.getStringExtra("Title");
			String popis = data.getStringExtra("Popis");
			this.list.add(new Item(R.drawable.ic_event_note, nazov, popis));
			setLayout();
			SavePreferences();


		}

		if (resultCode == RESULT_OK && requestCode == 2) {
			loadPreferences();
		}
		setLayout();
	}

	/**
	 * nacita z databazy data
	 */
	private void loadPreferences() {
		 this.list = this.storage.get();
	}

	/**
	 * ulozi data do databazy
	 */
	private void SavePreferences() {
		this.storage.set(this.list);
	}

	/**
	 * aktualizuje stav itemov v main activity a vykresli ich
	 */
	private void setLayout() {
		recyclerView = findViewById(R.id.recyclerView);
		recyclerView.setHasFixedSize(true);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		adapter = new Adapter(this.list, this);
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setAdapter(adapter);
	}

	/**
	 * Vyvola activitu ItemDefinition po kliknuti na Item
	 *
	 * @param position pozicia itemu na ktory sa kliklo
	 */
	@Override
	public void onItemClick(int position) {
		Intent intent = new Intent(MainActivity.this, ItemDefinition.class);
		intent.putExtra("selected_item_index", position + "");
		startActivityForResult(intent, 2);
	}

	/**
	 * Vytvori sa menu a v nom searchView
	 *
	 * @param menu toolbar menu
	 * @return false
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.my_menu, menu);
		final MenuItem searchItem = menu.findItem(R.id.action_search);
		final SearchView searchView = (SearchView) searchItem.getActionView();
		searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
		searchView.setQueryHint("Hľadať poznámku");
		searchView.onActionViewExpanded();
		searchView.setIconified(false);
		searchView.performClick();

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			/**
			 * zavola sa pri potvrdeni textu v searchView
			 * @param query zadavany text
			 * @return false
			 */
			@Override
			public boolean onQueryTextSubmit(String query) {
				adapter.getFilter().filter(query);
				return false;
			}

			/**
			 * vola sa pri kazdej zmene textu v searchview
			 * @param newText zadavany text
			 * @return false
			 */
			@Override
			public boolean onQueryTextChange(String newText) {
				adapter.getFilter().filter(newText);
				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}
}

