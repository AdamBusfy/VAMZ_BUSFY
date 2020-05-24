package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Trieda adapter
 *
 * <code>Adapter</code> extenduje RecyclerView na ktorom prekryva metody, spaja data s recyclerView-om
 */
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements Filterable {

	private ArrayList<Item> list;
	private ArrayList<Item> listFull;
	private OnItemListener onItemListener;

	public Adapter(ArrayList<Item> exampleList, OnItemListener onItemListener) {
		this.list = exampleList;
		this.onItemListener = onItemListener;
		this.listFull = new ArrayList<>(list);      //this.listFull = new ArrayList<>(list);
		//listFull = list;
	}

	/**
	 * Vnorena trieda ViewHolder, ktora extenduje ViewHolder
	 */
	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		public ImageView mImageView;
		public TextView mTextView1;
		public TextView mTextView2;
		OnItemListener onItemListener;

		public ViewHolder(View itemView, OnItemListener onItemListener) {
			super(itemView);
			mImageView = itemView.findViewById(R.id.obrazokPoznamky);
			mTextView1 = itemView.findViewById(R.id.title);
			mTextView2 = itemView.findViewById(R.id.description);

			this.onItemListener = onItemListener;
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			onItemListener.onItemClick(getAdapterPosition());
		}
	}

	/**
	 * Interface, ktory specifikuje spravanie listenera
	 */
	public interface OnItemListener {
		void onItemClick(int position);
	}

	/**
	 * Nastavi layout viewHolderu
	 *
	 * @param parent viewGroup do ktoreho bude novy viewHolder pridany
	 * @param viewType typ view
	 * @return novy viewHolder
	 */
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
		ViewHolder evh = new ViewHolder(v, onItemListener);
		return evh;
	}

	/**
	 * Metoda volana recyclerView-om aby zobrazil data na specifickej pozicii
	 *
	 * @param holder ktory by mal byt update-ovany
	 * @param position pozicia itemu
	 */
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Item currentItem = this.list.get(position);
		holder.mImageView.setImageResource(currentItem.getImageResource());
		holder.mTextView1.setText(currentItem.getMenoPoznamky());
		holder.mTextView2.setText(currentItem.getPopisPoznamky());
	}

	/**
	 * Vrati pocet itemov
	 *
	 * @return pocet itemov
	 */
	@Override
	public int getItemCount() {
		return null != this.list ? this.list.size() : 0;
	}

	/**
	 * Vrati filter
	 * @return filter
	 */
	@Override
	public Filter getFilter() {
		return filter;
	}


	private Filter filter = new Filter() {
		/**
		 * Metoda na filtrovanie
		 *
		 * @param constraint sekvencia pismen na zaklade ktorej sa filtruje
		 * @return vyfiltrovane itemy
		 */
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			List<Item> filteredList = new ArrayList<>();
			if (constraint == null || constraint.length() == 0) {
				filteredList.addAll(listFull);
			} else {
				String filterPattern = constraint.toString().toLowerCase().trim();
				for (Item item : listFull) {
					System.out.println(item);
					if ((item.getPopisPoznamky().toLowerCase().contains(filterPattern)) || (item.getMenoPoznamky().toLowerCase().contains(filterPattern))) {
						filteredList.add(item);
					}
				}
			}
			FilterResults results = new FilterResults();
			results.values = filteredList;
			return results;
		}

		/**
		 * Metoda zverejni vysledok filtrovania
		 *
		 * @param constraint sekvencia pismen na zaklade ktorej sa filtrovalo
		 * @param results vysledok filtrovania
		 */
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
				list.clear();
				list.addAll((List) results.values);
				notifyDataSetChanged();
		}
	};
}
