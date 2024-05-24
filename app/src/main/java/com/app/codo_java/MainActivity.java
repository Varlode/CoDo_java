package com.app.codo_java;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.codo_java.data.LocalDataSources;
import com.app.codo_java.data.RemoteDataSources;
import com.app.codo_java.model.User;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	Button updateButton;
	Button readButton;
	Button setupButton;
	Button totalButton;
	TextView profileNameOutput;
	TextView profileRoleOutput;
	LocalDataSources localDS;
	RemoteDataSources remoteDS;
	User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		localDS = new LocalDataSources(getSharedPreferences("CODO", MODE_PRIVATE));
		remoteDS = new RemoteDataSources(FirebaseDatabase.getInstance());

		remoteDS.getUser("users/" + localDS.getLoginUsername(), userResult -> {
			this.user = userResult;
			profileNameOutput.setText(userResult.getName());
			profileRoleOutput.setText(userResult.getRole());
		});

		updateButton = findViewById(R.id.mainUpdateButton);
		readButton = findViewById(R.id.mainReadButton);
		totalButton = findViewById(R.id.mainTotalButton);
		setupButton = findViewById(R.id.mainSetupButton);
		profileNameOutput = findViewById(R.id.mainProfileNameOutput);
		profileRoleOutput = findViewById(R.id.mainProfileRoleOutput);

		updateButton.setOnClickListener(this);
		readButton.setOnClickListener(this);
		setupButton.setOnClickListener(this);
		totalButton.setOnClickListener(this);
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.mainUpdateButton:
				intent = new Intent(this, UpdateActivity.class);
				startActivity(intent);
				break;

			case R.id.mainReadButton:
				intent = new Intent(this, ReadActivity.class);
				startActivity(intent);
				break;

			case R.id.mainTotalButton:
				intent = new Intent(this, TotalActivity.class);
				startActivity(intent);
				break;

			case R.id.mainSetupButton:
				if (permissionNotify())
					break;
				intent = new Intent(this, SetupActivity.class);
				startActivity(intent);
				break;

			default:
				break;
		}
	}

	private boolean permissionNotify() {
		String role = profileRoleOutput.getText().toString();
		if (role.equals("Quản trị viên") || role.equals("Ban chấp hành"))
			return false;
		Toast.makeText(this, "Bạn không có quyền truy cập", Toast.LENGTH_SHORT).show();
		return true;
	}

}
