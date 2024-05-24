package com.app.codo_java;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.codo_java.adapter.RecyclerUnitAdapter;
import com.app.codo_java.data.RemoteDataSources;
import com.app.codo_java.model.Fault;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import kotlin.Triple;

public class TotalActivity extends AppCompatActivity {


	RemoteDataSources remoteDS;

	ImageButton backButton;
	TextView dateFrom;
	TextView dateTo;
	Button dateFromButton;
	Button dateToButton;
	DatePicker datePicker;
	CardView datePickerCardView;
	Button totalButton;
	RecyclerView recyclerView;
	ProgressDialog dialog;

	@Override
	protected void onCreate(
		@Nullable
		Bundle savedInstanceState
	)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_total);

		backButton = findViewById(R.id.totalBackButton);
		dateFromButton = findViewById(R.id.totalFilterDateFromButton);
		dateToButton = findViewById(R.id.totalFilterDateToButton);
		dateFrom = findViewById(R.id.totalFilterDateFromInput);
		dateTo = findViewById(R.id.totalFilterDateToInput);
		datePicker = findViewById(R.id.totalDatePicker);
		datePickerCardView = findViewById(R.id.totalDatePickerCardView);
		recyclerView = findViewById(R.id.totalRecyclerView);
		totalButton = findViewById(R.id.totalFilterButton);

		dialog = new ProgressDialog(this);
		dialog.setMessage("Đang tổng điểm...");

		remoteDS = new RemoteDataSources(FirebaseDatabase.getInstance());

		String today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
		dateTo.setText(today);
		dateFrom.setText(today);

		backButton.setOnClickListener(this::onClick);
		dateFromButton.setOnClickListener(this::onClick);
		dateToButton.setOnClickListener(this::onClick);
		dateFrom.setOnClickListener(this::onClick);
		dateTo.setOnClickListener(this::onClick);
		totalButton.setOnClickListener(this::total);
	}

	@SuppressLint("NonConstantResourceId")
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.totalBackButton:
				onBackPressed();
				finish();
				break;

			case R.id.totalFilterDateFromInput:
				dateToButton.setVisibility(View.INVISIBLE);
				datePickerCardView.setVisibility(View.VISIBLE);
				dateFromButton.setVisibility(View.VISIBLE);
				break;

			case R.id.totalFilterDateFromButton:
				dateFrom.setText(getDate());
				datePickerCardView.setVisibility(View.INVISIBLE);
				dateFromButton.setVisibility(View.INVISIBLE);
				break;

			case R.id.totalFilterDateToInput:
				dateFromButton.setVisibility(View.INVISIBLE);
				datePickerCardView.setVisibility(View.VISIBLE);
				dateToButton.setVisibility(View.VISIBLE);
				break;

			case R.id.totalFilterDateToButton:
				dateTo.setText(getDate());
				datePickerCardView.setVisibility(View.INVISIBLE);
				dateToButton.setVisibility(View.INVISIBLE);
				break;
		}
	}

	private void total(View v) {

		dialog.show();
		remoteDS.<String>getListOfValue("units/", result -> {

			HashMap<String, Long> myMap = new HashMap<>();
			for (String string : result) {
				myMap.put(string, 1000L);
			}

			remoteDS.getListOfFault(
				"dashboard/", dateFrom.getText().toString(), dateTo.getText().toString(),
				null, null, null, faults -> {

					ArrayList<Triple<String, Long, Long>> triples = sortFaults(myMap, faults);

					RecyclerUnitAdapter adapter = new RecyclerUnitAdapter(this, triples);
					recyclerView.setLayoutManager(new LinearLayoutManager(
						this,
						LinearLayoutManager.VERTICAL,
						false
					));
					recyclerView.setAdapter(adapter);
					recyclerView.setHasFixedSize(true);
					dialog.dismiss();
				}
			);
		});
	}


	private ArrayList<Triple<String, Long, Long>> sortFaults(
		HashMap<String, Long> sources, ArrayList<Fault> faults
	)
	{

		for (Fault fault : faults) {
			String unit = fault.getUnit();
			sources.put(unit, sources.get(unit) + Integer.parseInt(fault.getPoint()));
		}

		ArrayList<Pair<String, Long>> pairs = new ArrayList<>();
		for (HashMap.Entry<String, Long> entry : sources.entrySet()) {
			pairs.add(new Pair<>(entry.getKey(), entry.getValue()));
		}

		pairs.sort(new Comparator<Pair<String, Long>>() {

			@Override
			public int compare(Pair<String, Long> o1, Pair<String, Long> o2) {
				return -o1.second.compareTo(o2.second);
			}
		});

		ArrayList<Triple<String, Long, Long>> triples = new ArrayList<>();
		long rank = 0;
		long preVal = -100;

		for (Pair<String, Long> pair : pairs) {
			if (pair.second != preVal)
				rank++;
			preVal = pair.second;
			triples.add(new Triple<>(pair.first, pair.second, rank));
		}

		triples.sort(new Comparator<Triple<String, Long, Long>>() {

			@Override
			public int compare(Triple<String, Long, Long> o1, Triple<String, Long, Long> o2) {
				return o1.component1().compareTo(o2.component1());
			}
		});
		return triples;
	}

	private String getDate() {
		int nDay = datePicker.getDayOfMonth();
		String day = String.valueOf(nDay);
		if (nDay < 10)
			day = "0" + day;
		int nMonth = datePicker.getMonth() + 1;
		String month = String.valueOf(nMonth);
		if (nMonth < 10)
			month = "0" + month;
		String year = String.valueOf(datePicker.getYear());
		return day + "/" + month + "/" + year;
	}

}

	
