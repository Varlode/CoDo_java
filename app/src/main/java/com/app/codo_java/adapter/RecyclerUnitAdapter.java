package com.app.codo_java.adapter;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.codo_java.R;

import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.List;

import kotlin.Triple;

public class RecyclerUnitAdapter extends RecyclerView.Adapter<RecyclerUnitAdapter.ViewUnitHolder> {


	private Context context;
	private ArrayList<Triple<String, Long, Long>> mList;

	public RecyclerUnitAdapter(
		Context context, ArrayList<Triple<String, Long, Long>> mList
	)
	{
		this.mList = mList;
		this.context = context;
	}

	@NonNull
	@Override
	public RecyclerUnitAdapter.ViewUnitHolder onCreateViewHolder(
		@NonNull
		ViewGroup parent, int viewType
	)
	{
		return new ViewUnitHolder(
			LayoutInflater.from(parent.getContext()).inflate(R.layout.item_unit, parent, false));
	}

	@Override
	public void onBindViewHolder(
		@NonNull
	 	RecyclerUnitAdapter.ViewUnitHolder holder, int position
	)
	{
		holder.unit.setText(mList.get(position).component1());
		holder.points.setText(String.valueOf(mList.get(position).component2()));
		holder.rank.setText(String.valueOf(mList.get(position).component3()));
	}

	@Override
	public int getItemCount() {
		return mList == null ? 0 : mList.size();
	}

	public static class ViewUnitHolder extends RecyclerView.ViewHolder {

		TextView unit;
		TextView points;
		TextView rank;

		public ViewUnitHolder(
			@NonNull
			View itemView
		)
		{
			super(itemView);

			unit = itemView.findViewById(R.id.itemUnitText);
			points = itemView.findViewById(R.id.itemPointText);
			rank = itemView.findViewById(R.id.itemRankText);
		}

	}

}
