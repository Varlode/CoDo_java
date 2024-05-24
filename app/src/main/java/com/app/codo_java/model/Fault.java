package com.app.codo_java.model;

public class Fault {

	String fault;
	String name;
	String unit;
	String photo;
	String date;
	String time;
	String nameInput;
	String unitInput;
	String roleInput;
	String point;

	public Fault() {}

	public Fault(
		String fault, String name, String unit, String photo, String date, String time,
		String nameInput, String unitInput, String roleInput, String point
	)
	{
		this.fault = fault;
		this.name = name;
		this.unit = unit;
		this.photo = photo;
		this.date = date;
		this.time = time;
		this.nameInput = nameInput;
		this.unitInput = unitInput;
		this.roleInput = roleInput;
		this.point = point;
	}

	public int size() {return 10;}

	public String getFault() {
		return fault;
	}

	public String getName() {
		return name;
	}

	public String getUnit() {
		return unit;
	}

	public String getPhoto() {
		return photo;
	}

	public String getDate() {
		return date;
	}

	public String getTime() {
		return time;
	}

	public String getNameInput() {
		return nameInput;
	}

	public String getUnitInput() {
		return unitInput;
	}

	public String getRoleInput() {
		return roleInput;
	}

	public String getPoint() {return point;}

}
