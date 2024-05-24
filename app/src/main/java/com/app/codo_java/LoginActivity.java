package com.app.codo_java;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.codo_java.data.LocalDataSources;
import com.app.codo_java.data.RemoteDataSources;
import com.app.codo_java.data.UserRepository;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

	Button loginButton;
	EditText usernameInput;
	EditText passwordInput;
	LocalDataSources localDS;
	RemoteDataSources remoteDS;
	UserRepository userRepo;
	ProgressDialog dialog;
	ConstraintLayout mLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		localDS = new LocalDataSources(getSharedPreferences("CODO", MODE_PRIVATE));
		remoteDS = new RemoteDataSources(FirebaseDatabase.getInstance());

		dialog = new ProgressDialog(this);
		dialog.setTitle("Đăng nhập");
		dialog.setMessage("Đang xử lý...");

		userRepo = new UserRepository(localDS, remoteDS);

		loginButton = findViewById(R.id.loginLoginButton);
		usernameInput = findViewById(R.id.loginUsernameInput);
		passwordInput = findViewById(R.id.loginPasswordInput);
		mLayout = findViewById(R.id.login_layout);

		loginButton.setOnClickListener(this::login);
		mLayout.setOnClickListener(this::hideKeyBoard);

		usernameInput.setText(localDS.getLoginUsername());
		passwordInput.setText(localDS.getLoginPassword());
	}

	private void login(View view) {
		String username = usernameInput.getText().toString();
		String password = passwordInput.getText().toString();

		if (username.isEmpty() || password.isEmpty()) {
			inValidLoginNotify();
			return;
		}

		Intent intent = new Intent(this, MainActivity.class);

		dialog.show();
		userRepo.login(username, password, isSuccess -> {
			if (isSuccess) {
				startActivity(intent);
				Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
			}
			dialog.dismiss();
		});
	}

	private void hideKeyBoard(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(
			INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	private void inValidLoginNotify() {
		Toast.makeText(
			this,
			"Tên đăng nhập và Mật khẩu không thể để trống",
			Toast.LENGTH_SHORT
		).show();
	}

}