package com.app.codo_java.model;

public class User {

	private String name;
	private String username;
	private String role;
	private String unit;
	private String password;

	public User() {}

	public User(
		String name, String username, String role, String unit, String password
	)
	{
		this.name = name;
		this.username = username;
		this.role = role;
		this.unit = unit;
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public String getUsername() {
		return username;
	}

	public String getRole() {
		return role;
	}

	public String getUnit() {
		return unit;
	}

	public String getPassword() {
		return password;
	}

}
