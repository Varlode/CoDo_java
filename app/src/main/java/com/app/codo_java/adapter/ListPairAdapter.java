package com.app.codo_java.adapter;

import static com.app.codo_java.R.layout.item_spinner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.codo_java.R;

import java.util.ArrayList;

public class ListPairAdapter<E, V> extends BaseAdapter {

	private final ArrayList<Pair<E, V>> mList;
	private final Context context;

	public ListPairAdapter(Context context, ArrayList<Pair<E, V>> inputList) {
		this.context = context;
		this.mList = inputList;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Pair<E, V> getItem(int position) {
		return (Pair<E, V>) mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@NonNull
	@Override
	public View getView(
		int position, View convertView,
		@NonNull
		ViewGroup parent
	)
	{
		@SuppressLint("ViewHolder")
		View view = LayoutInflater.from(context).inflate(item_spinner, parent, false);

		TextView faultName = view.findViewById(R.id.itemSpinnerName);
		TextView faultPoint = view.findViewById(R.id.itemSpinnerPoint);

		Pair<E, V> item = mList.get(position);
		faultName.setText(item.first.toString());
		faultPoint.setText(item.second.toString());

		return view;
	}

}
