package com.app.codo_java;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.codo_java.data.RemoteDataSources;
import com.app.codo_java.model.User;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SetupActivity extends AppCompatActivity implements View.OnClickListener {

	ImageButton backButton;

	//    user
	EditText nameInput;
	EditText usernameInput;
	EditText passwordInput;
	Spinner userRoleSpinner;
	Spinner userUnitSpinner;
	Button setupUserButton;
	Button deleteUserButton;
	//    unit
	EditText unitInput;
	Button setupUnitButton;
	Button deleteUnitButton;
	//    role
	EditText roleInput;
	Button setupRoleButton;
	Button deleteRoleButton;
	//    fault
	Spinner faultRoleSpinner;
	EditText faultInput;
	Button setupFaultButton;
	Button deleteFaultButton;
	EditText pointInput;

	ConstraintLayout mLayout;

	RemoteDataSources remoteDS;
	ProgressDialog dialog;

	@Override
	protected void onCreate(
		@Nullable Bundle savedInstanceState
	)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup);

		remoteDS = new RemoteDataSources(FirebaseDatabase.getInstance());

		dialog = new ProgressDialog(this);
		dialog.setTitle("Thiết lập");
		dialog.setMessage("Đang xử lý...");

		backButton = findViewById(R.id.setupBackButton);

		nameInput = findViewById(R.id.setupNameInput);
		usernameInput = findViewById(R.id.setupUsernameInput);
		passwordInput = findViewById(R.id.setupPasswordInput);
		userRoleSpinner = findViewById(R.id.setupUserRoleSpinner);
		userUnitSpinner = findViewById(R.id.setupUserUnitSpinner);
		setupUserButton = findViewById(R.id.setupUserButton);
		deleteUserButton = findViewById(R.id.deleteUserButton);

		unitInput = findViewById(R.id.setupUnitInput);
		setupUnitButton = findViewById(R.id.setupUnitButton);
		deleteUnitButton = findViewById(R.id.deleteUnitButton);

		roleInput = findViewById(R.id.setupRoleInput);
		setupRoleButton = findViewById(R.id.setupRoleButton);
		deleteRoleButton = findViewById(R.id.deleteRoleButton);

		faultRoleSpinner = findViewById(R.id.setupFaultRoleSpinner);
		faultInput = findViewById(R.id.setupFaultInput);
		pointInput = findViewById(R.id.setuptPointInput);
		setupFaultButton = findViewById(R.id.setupFaultButton);
		deleteFaultButton = findViewById(R.id.deleteFaultButton);

		mLayout = findViewById(R.id.setup_layout);

		mLayout.setOnClickListener(this);
		backButton.setOnClickListener(this);
		setupUserButton.setOnClickListener(this);
		deleteUserButton.setOnClickListener(this);
		setupUnitButton.setOnClickListener(this);
		deleteUnitButton.setOnClickListener(this);
		setupRoleButton.setOnClickListener(this);
		deleteRoleButton.setOnClickListener(this);
		setupFaultButton.setOnClickListener(this);
		deleteFaultButton.setOnClickListener(this);

		dialog.show();
		remoteDS.<String>getListOfValue("roles/", result -> {
			result.add("Vai trò");
			ArrayAdapter<String> adapter = new ArrayAdapter<>(
				this, android.R.layout.simple_spinner_item, result);
			adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
			userRoleSpinner.setAdapter(adapter);
			userRoleSpinner.setSelection(adapter.getCount() - 1);
			faultRoleSpinner.setAdapter(adapter);
			faultRoleSpinner.setSelection(adapter.getCount() - 1);
			dialog.dismiss();
		});

		dialog.show();
		remoteDS.<String>getListOfValue("units/", result -> {
			result.add("Đơn vị");
			ArrayAdapter<String> adapter = new ArrayAdapter<>(
				this, android.R.layout.simple_spinner_item, result);
			adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
			userUnitSpinner.setAdapter(adapter);
			userUnitSpinner.setSelection(adapter.getCount() - 1);
			dialog.dismiss();
		});
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(@NonNull View v) {
		String unit, role, fault, point, name, username, password;
		int unitId, unitLast, roleId, roleLast;

		switch (v.getId()) {
			case R.id.setupBackButton:
				onBackPressed();
				finish();
				break;

			case R.id.setupUserButton:
				name = nameInput.getText().toString();
				username = usernameInput.getText().toString();
				password = passwordInput.getText().toString();
				unitId = userUnitSpinner.getSelectedItemPosition();
				roleId = userRoleSpinner.getSelectedItemPosition();
				unitLast = userUnitSpinner.getAdapter().getCount() - 1;
				roleLast = userRoleSpinner.getAdapter().getCount() - 1;

				if (name.isEmpty() || username.isEmpty() || password.isEmpty() ||
				    unitId == unitLast || roleId == roleLast)
				{
					blankInvalidNotify();
					break;
				}

				dialog.show();
				remoteDS.updateUser("users/" + username, getUser(), this::updateNotify);
				nameInput.setText("");
				usernameInput.setText("");
				passwordInput.setText("");
				break;

			case R.id.deleteUserButton:
				username = usernameInput.getText().toString();
				if (username.isEmpty()) {
					blankInvalidNotify();
					break;
				}
				dialog.show();
				remoteDS.deleteUser("users/" + username, this::deleteNotify);
				nameInput.setText("");
				usernameInput.setText("");
				passwordInput.setText("");
				break;

			case R.id.setupUnitButton:
				unit = unitInput.getText().toString();
				if (unit.isEmpty()) {
					blankInvalidNotify();
					break;
				}
				dialog.show();
				remoteDS.<String>updateValue("units/" + unit, unit, this::updateNotify);
				unitInput.setText("");
				break;

			case R.id.deleteUnitButton:
				unit = unitInput.getText().toString();
				if (unit.isEmpty()) {
					blankInvalidNotify();
					break;
				}
				dialog.show();
				remoteDS.deleteValue("units/" + unit, this::deleteNotify);
				unitInput.setText("");
				break;

			case R.id.setupRoleButton:
				role = roleInput.getText().toString();
				if (role.isEmpty()) {
					blankInvalidNotify();
					break;
				}

				dialog.show();
				remoteDS.<String>updateValue("roles/" + role, role, this::updateNotify);
				roleInput.setText("");
				break;

			case R.id.deleteRoleButton:
				role = roleInput.getText().toString();
				if (role.isEmpty()) {
					blankInvalidNotify();
					break;
				}
				dialog.show();
				remoteDS.deleteValue("roles/" + role, this::deleteNotify);
				roleInput.setText("");
				break;

			case R.id.setupFaultButton:
				fault = faultInput.getText().toString();
				point = pointInput.getText().toString();
				role = faultRoleSpinner.getSelectedItem().toString();
				roleId = faultRoleSpinner.getSelectedItemPosition();
				roleLast = faultRoleSpinner.getAdapter().getCount() - 1;

				if (fault.isEmpty() || roleId == roleLast) {
					blankInvalidNotify();
					break;
				}
				dialog.show();
				String path = "faults/" + role + "/" + fault;
				HashMap<String, String> myMap = new HashMap<>();
				myMap.put("name", fault);
				myMap.put("point", point);
				remoteDS.updateMap(path, myMap, this::updateNotify);
				faultInput.setText("");
				break;

			case R.id.deleteFaultButton:
				fault = faultInput.getText().toString();
				role = faultRoleSpinner.getSelectedItem().toString();
				roleId = faultRoleSpinner.getSelectedItemPosition();
				roleLast = faultRoleSpinner.getAdapter().getCount() - 1;

				if (fault.isEmpty() || roleId == roleLast) {
					blankInvalidNotify();
					break;
				}
				dialog.show();
				remoteDS.deleteValue("faults/" + role, this::deleteNotify);
				faultInput.setText("");
				break;

			case R.id.setup_layout:
				InputMethodManager imm = (InputMethodManager) getSystemService(
					INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

			default:
				break;
		}
	}

	private User getUser() {
		String name = nameInput.getText().toString(), username = usernameInput
			                                                         .getText()
			                                                         .toString(), password = passwordInput
				                                                                                 .getText()
				                                                                                 .toString(), role = userRoleSpinner
					                                                                                                     .getSelectedItem()
					                                                                                                     .toString(), unit = userUnitSpinner
						                                                                                                                         .getSelectedItem()
						                                                                                                                         .toString();

		return new User(name, username, role, unit, password);
	}

	private void updateNotify(boolean isSuccess) {
		if (isSuccess)
			Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
		dialog.dismiss();
	}

	private void deleteNotify(boolean isSuccess) {
		if (isSuccess)
			Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
		dialog.dismiss();
	}

	private void blankInvalidNotify() {
		Toast.makeText(this, "Không thể để trống", Toast.LENGTH_SHORT).show();
	}

}
