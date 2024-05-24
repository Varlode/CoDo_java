package com.app.codo_java.data;

import android.content.SharedPreferences;

public class LocalDataSources {

	private final SharedPreferences loRef;

	public LocalDataSources(SharedPreferences localDataSources) {
		this.loRef = localDataSources;
	}

	public void setLogin(String username, String password) {
		SharedPreferences.Editor editor = loRef.edit();
		editor.putString("KEY_LOGIN_USERNAME", username);
		editor.putString("KEY_LOGIN_PASSWORD", password);
		editor.apply();
	}

	public String getLoginUsername() {
		return loRef.getString("KEY_LOGIN_USERNAME", "");
	}

	public String getLoginPassword() {
		return loRef.getString("KEY_LOGIN_PASSWORD", "");
	}

}
