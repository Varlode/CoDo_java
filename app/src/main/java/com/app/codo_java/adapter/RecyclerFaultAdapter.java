package com.app.codo_java.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.codo_java.R;
import com.app.codo_java.model.Fault;
import com.bumptech.glide.Glide;

import java.util.List;

public class RecyclerFaultAdapter
	extends RecyclerView.Adapter<RecyclerFaultAdapter.ViewFaultHolder>
{

	private final List<Fault> mList;
	private final Context context;

	public RecyclerFaultAdapter(Context context, List<Fault> faultList) {
		this.mList = faultList;
		this.context = context;
	}

	@NonNull
	@Override
	public RecyclerFaultAdapter.ViewFaultHolder onCreateViewHolder(
		@NonNull
		ViewGroup parent, int viewType
	)
	{
		return new ViewFaultHolder(
			LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fault, parent, false));
	}


	@Override
	public void onBindViewHolder(
		@NonNull
		RecyclerFaultAdapter.ViewFaultHolder holder, int position
	)
	{
		holder.fault.setText(mList.get(position).getFault());
		holder.unit.setText(mList.get(position).getUnit());
		holder.name.setText(mList.get(position).getName());
		holder.headerSource.setText(mList.get(position).getNameInput());
		holder.headerTime.setText(mList.get(position).getTime());
		holder.headerDate.setText(mList.get(position).getDate());
		holder.headerUnit.setText(mList.get(position).getUnitInput());
		holder.headerRole.setText(mList.get(position).getRoleInput());
		holder.point.setText(String.valueOf(mList.get(position).getPoint()));
		Glide.with(context).load(mList.get(position).getPhoto()).into(holder.imageView);
	}

	@Override
	public int getItemCount() {
		return mList == null ? 0 : mList.size();
	}

	public static class ViewFaultHolder extends RecyclerView.ViewHolder {

		TextView headerSource;
		TextView headerUnit;
		TextView headerRole;
		TextView headerDate;
		TextView headerTime;
		TextView name;
		TextView unit;
		TextView fault;
		TextView point;
		ImageView imageView;

		public ViewFaultHolder(
			@NonNull
			View itemView
		)
		{
			super(itemView);

			headerSource = itemView.findViewById(R.id.faultItemHeaderSource);
			headerRole = itemView.findViewById(R.id.faultItemHeaderRole);
			headerUnit = itemView.findViewById(R.id.faultItemHeaderUnit);
			headerDate = itemView.findViewById(R.id.faultItemHeaderDate);
			headerTime = itemView.findViewById(R.id.faultItemHeaderTime);
			name = itemView.findViewById(R.id.faultItemName);
			unit = itemView.findViewById(R.id.faultItemUnit);
			fault = itemView.findViewById(R.id.faultItemFault);
			point = itemView.findViewById(R.id.faultItemPoint);
			imageView = itemView.findViewById(R.id.faultItemImage);
		}

	}

}
