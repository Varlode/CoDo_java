package com.app.codo_java;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.app.codo_java.adapter.ListPairAdapter;
import com.app.codo_java.data.LocalDataSources;
import com.app.codo_java.data.RemoteDataSources;
import com.app.codo_java.model.Fault;
import com.app.codo_java.model.User;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener {

	private static final int REQUEST_CODE = 96;
	private static final int CAMERA_CODE = 23;
	private static final String REF_DATES = "01/01/2020";
	RemoteDataSources remoteDS;
	LocalDataSources localDS;
	Fault faultData;
	ArrayAdapter<String> adapter;
	ProgressDialog dialog;
	ImageButton backButton;
	Spinner faultSpinner;
	EditText nameInput;
	Spinner unitSpinner;
	ImageView imageInput;
	Button cameraButton;
	Button updateButton;
	User user;

	ConstraintLayout mLayout;

	@Override
	protected void onCreate(
		@Nullable
		Bundle savedInstanceState
	)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);

		remoteDS = new RemoteDataSources(
			FirebaseDatabase.getInstance(),
			FirebaseStorage.getInstance()
		);

		localDS = new LocalDataSources(getSharedPreferences("CODO", MODE_PRIVATE));

		dialog = new ProgressDialog(this);
		dialog.setMessage("Đang xử lý...");

		backButton = findViewById(R.id.updateBackButton);
		faultSpinner = findViewById(R.id.updateFaultSpinner);
		nameInput = findViewById(R.id.updateNameInput);
		unitSpinner = findViewById(R.id.updateUnitSpinner);
		imageInput = findViewById(R.id.updateImageView);
		cameraButton = findViewById(R.id.updateCameraButton);
		updateButton = findViewById(R.id.updateUpdateButton);
		mLayout = findViewById(R.id.update_layout);

		mLayout.setOnClickListener(this);
		backButton.setOnClickListener(this);
		cameraButton.setOnClickListener(this);
		updateButton.setOnClickListener(this::update);

		imageInput.setDrawingCacheEnabled(true);
		imageInput.buildDrawingCache();

		Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.person);
		imageInput.setImageBitmap(icon);

		dialog.show();
		remoteDS.getUserSingle("users/" + localDS.getLoginUsername(), user -> {
			this.user = user;
			String role = user.getRole();
			remoteDS.<String, String>getListOfPair("faults/" + role, result -> {
				result.add(Pair.create("Lỗi vi phạm", " "));
				ListPairAdapter<String, String> adapter = new ListPairAdapter<>(this, result);
				faultSpinner.setAdapter(adapter);
				faultSpinner.setSelection(adapter.getCount() - 1);
				dialog.dismiss();
			});
		});

		dialog.show();
		remoteDS.<String>getListOfValue("units/", result -> {
			result.add("Đơn vị");
			ArrayAdapter<String> adapter = new ArrayAdapter<>(
				this, android.R.layout.simple_spinner_item, result);
			adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
			unitSpinner.setAdapter(adapter);
			unitSpinner.setSelection(adapter.getCount() - 1);
			dialog.dismiss();
		});
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.updateBackButton:
				onBackPressed();
				finish();
				break;

			case R.id.updateCameraButton:
				if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
				    PackageManager.PERMISSION_GRANTED)
				{
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, REQUEST_CODE);
				} else {
					ActivityCompat.requestPermissions(
						this,
						new String[] {Manifest.permission.CAMERA},
						CAMERA_CODE
					);
				}
				break;

			case R.id.update_layout:
				InputMethodManager imm = (InputMethodManager) getSystemService(
					INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				break;

			default:
				break;
		}

	}

	@Override
	public void onRequestPermissionsResult(
		int requestCode,
		@NonNull
		String[] permissions,
		@NonNull
		int[] grantResults
	)
	{
		if (requestCode == CAMERA_CODE && grantResults.length > 0 &&
		    grantResults[0] == PackageManager.PERMISSION_GRANTED)
		{
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, REQUEST_CODE);
		} else {
			Toast.makeText(
				this,
				"Quyền truy cập máy ảnh bị từ chối",
				Toast.LENGTH_SHORT
			).show();
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	@Override
	protected void onActivityResult(
		int requestCode, int resultCode,
		@Nullable
		Intent data
	)
	{
		if (requestCode == REQUEST_CODE && data != null && resultCode == RESULT_OK) {
			Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
			imageInput.setImageBitmap(photo);
		} else {
			Toast.makeText(this, "Máy ảnh bị dừng", Toast.LENGTH_SHORT).show();
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void update(View v) {
		String name = nameInput.getText().toString();
		int unitId = unitSpinner.getSelectedItemPosition(),
			faultId = faultSpinner.getSelectedItemPosition(),
			unitLast = unitSpinner.getAdapter().getCount() - 1,
			faultLast = faultSpinner.getAdapter().getCount() - 1;
		Bitmap bitmap = ((BitmapDrawable) imageInput.getDrawable()).getBitmap();

		if (name.isEmpty() || unitId == unitLast || faultId == faultLast) {
			Toast.makeText(this, "Không thể để trống", Toast.LENGTH_SHORT).show();
			return;
		}

		dialog.show();
		remoteDS.updateImage("images/", bitmap, imgPath -> {

			String
				today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()),
				now = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

			long daysBetween = theDaysBetween(today);

			Pair<String, String> pair = (Pair<String, String>) faultSpinner.getSelectedItem();

			String
				fault = pair.first,
				point = pair.second,
				unit = unitSpinner.getSelectedItem().toString();

			faultData = new Fault(
				fault, name, unit, imgPath, today, now, user.getName(),
				user.getUnit(), user.getRole(), point
			);

			remoteDS.updateFault("dashboard/" + daysBetween, faultData, isSuccess -> {
				if (isSuccess) {
					Toast.makeText(this, "Nhập thành công", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(this, "Nhập thất bại", Toast.LENGTH_SHORT).show();
				}
				dialog.dismiss();
			});
		});
	}

	private long theDaysBetween(String today) {
		long daysBetween = 1;
		try {
			@SuppressLint("SimpleDateFormat")
			Date refDate = new SimpleDateFormat("dd/MM/yyyy").parse(REF_DATES);
			@SuppressLint("SimpleDateFormat")
			Date nowDate = new SimpleDateFormat("dd/MM/yyyy").parse(today);
			long diff = nowDate.getTime() - refDate.getTime();
			daysBetween = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		return daysBetween;
	}

}
