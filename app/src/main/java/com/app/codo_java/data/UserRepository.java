package com.app.codo_java.data;

public class UserRepository {

	private final RemoteDataSources reRef;
	private final LocalDataSources loRef;

	public UserRepository(LocalDataSources localDataSources, RemoteDataSources remoteDataSources) {
		this.loRef = localDataSources;
		this.reRef = remoteDataSources;
	}

	public void login(String username, String password, LoginCallback callback) {
		reRef.getUserSingle("users/" + username, userResult -> {
			if (userResult == null) {
				callback.loginSuccess(false);
				return;
			}
			if (!userResult.getPassword().equals(password)) {
				callback.loginSuccess(false);
				return;
			}
			loRef.setLogin(username, password);
			callback.loginSuccess(true);
		});
	}

	public interface LoginCallback {

		void loginSuccess(boolean isSuccess);

	}

}
