package com.app.codo_java;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.codo_java.adapter.ListPairAdapter;
import com.app.codo_java.adapter.RecyclerFaultAdapter;
import com.app.codo_java.data.RemoteDataSources;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ReadActivity extends AppCompatActivity implements View.OnClickListener {

	RemoteDataSources remoteDS;

	ImageButton backButton;
	TextView dateFrom;
	TextView dateTo;
	Button dateFromButton;
	Button dateToButton;
	Button readButton;
	Spinner unitSpinner;
	Spinner faultSpinner;
	Spinner roleSpinner;
	DatePicker datePicker;
	CardView datePickerCardView;
	RecyclerView recyclerView;
	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read);

		remoteDS = new RemoteDataSources(FirebaseDatabase.getInstance());

		dialog = new ProgressDialog(this);
		dialog.setMessage("Đang thống kê...");

		backButton = findViewById(R.id.readBackButton);
		dateFromButton = findViewById(R.id.readFilterDateFromButton);
		dateToButton = findViewById(R.id.readFilterDateToButton);
		dateFrom = findViewById(R.id.readFilterDateFromInput);
		dateTo = findViewById(R.id.readFilterDateToInput);
		datePicker = findViewById(R.id.readDatePicker);
		roleSpinner = findViewById(R.id.readFilterRoleSpinner);
		unitSpinner = findViewById(R.id.readFilterUnitSpinner);
		faultSpinner = findViewById(R.id.readFilterFaultSpinner);
		datePickerCardView = findViewById(R.id.readDatePickerCardView);
		recyclerView = findViewById(R.id.readRecyclerView);
		readButton = findViewById(R.id.readFilterButton);

		backButton.setOnClickListener(this);
		dateFromButton.setOnClickListener(this);
		dateToButton.setOnClickListener(this);
		dateFrom.setOnClickListener(this);
		dateTo.setOnClickListener(this);
		readButton.setOnClickListener(this::read);

		String today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
		dateFrom.setText(today);
		dateTo.setText(today);

		dialog.show();
		remoteDS.<String, String>getListOfAllPair("faults/", result -> {
			result.add(Pair.create("Tất cả", " "));
			ListPairAdapter<String, String> adapter = new ListPairAdapter<>(this, result);
			faultSpinner.setAdapter(adapter);
			faultSpinner.setSelection(adapter.getCount() - 1);
			dialog.dismiss();
		});

		dialog.show();
		remoteDS.<String>getListOfValue("roles/", result -> {
			result.add("Tất cả");
			ArrayAdapter<String> adapter = new ArrayAdapter<>(
				this,
				android.R.layout.simple_spinner_item,
				result
			);
			adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
			roleSpinner.setAdapter(adapter);
			roleSpinner.setSelection(adapter.getCount() - 1);
			dialog.dismiss();
		});

		dialog.show();
		remoteDS.<String>getListOfValue("units/", result -> {
			result.add("Tất cả");
			ArrayAdapter<String> adapter = new ArrayAdapter<>(
				this,
				android.R.layout.simple_spinner_item,
				result
			);
			adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
			unitSpinner.setAdapter(adapter);
			unitSpinner.setSelection(adapter.getCount() - 1);
			dialog.dismiss();
		});

		dialog.show();
		remoteDS.getListOfFault(
			"dashboard/", dateFrom.getText().toString(), dateTo.getText().toString(),
			null, null, null, arrayList -> {
				RecyclerFaultAdapter adapter = new RecyclerFaultAdapter(this, arrayList);
				recyclerView.setLayoutManager(
					new LinearLayoutManager(
						this,
						LinearLayoutManager.VERTICAL,
						false
					));
				recyclerView.setAdapter(adapter);
				recyclerView.setHasFixedSize(true);
				dialog.dismiss();
			}
		);
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.readBackButton:
				onBackPressed();
				finish();
				break;

			case R.id.readFilterDateFromInput:
				dateToButton.setVisibility(View.INVISIBLE);
				datePickerCardView.setVisibility(View.VISIBLE);
				dateFromButton.setVisibility(View.VISIBLE);
				break;

			case R.id.readFilterDateFromButton:
				dateFrom.setText(getDate());
				datePickerCardView.setVisibility(View.INVISIBLE);
				dateFromButton.setVisibility(View.INVISIBLE);
				break;

			case R.id.readFilterDateToInput:
				dateFromButton.setVisibility(View.INVISIBLE);
				datePickerCardView.setVisibility(View.VISIBLE);
				dateToButton.setVisibility(View.VISIBLE);
				break;

			case R.id.readFilterDateToButton:
				dateTo.setText(getDate());
				datePickerCardView.setVisibility(View.INVISIBLE);
				dateToButton.setVisibility(View.INVISIBLE);
				break;

			default:
				break;
		}
	}

	private void read(View view) {
		Pair<String, String> faultPair = (Pair<String, String>) faultSpinner.getSelectedItem();
		String fault = faultPair.first;
		String role = roleSpinner.getSelectedItem().toString();
		String unit = unitSpinner.getSelectedItem().toString();
		if (Objects.equals(fault, "Tất cả"))
			fault = null;
		if (role.equals("Tất cả"))
			role = null;
		if (unit.equals("Tất cả"))
			unit = null;

		dialog.show();
		remoteDS.getListOfFault(
			"dashboard/", dateFrom.getText().toString(), dateTo.getText().toString(),
			fault, role, unit, arrayList -> {
				RecyclerFaultAdapter adapter = new RecyclerFaultAdapter(this, arrayList);
				recyclerView.setLayoutManager(
					new LinearLayoutManager(
						this,
						LinearLayoutManager.VERTICAL,
						false
					));
				recyclerView.setAdapter(adapter);
				recyclerView.setHasFixedSize(true);
				dialog.dismiss();
			}
		);
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
